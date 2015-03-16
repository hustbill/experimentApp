package worker;

import akka.actor.*;
import akka.contrib.pattern.ClusterClient;
import akka.contrib.pattern.ClusterSingletonManager;
import akka.dispatch.OnFailure;
import akka.dispatch.OnSuccess;
import akka.pattern.Patterns;
import akka.persistence.journal.leveldb.SharedLeveldbJournal;
import akka.persistence.journal.leveldb.SharedLeveldbStore;
import akka.util.Timeout;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class Main {
  public static void main(String[] args) throws InterruptedException {
    if (args.length == 0) {
    	System.out.println("** startBackend port 2551 ** ");
    	startBackend(2551, "backend");
      Thread.sleep(5000);
      System.out.println("** startBackend port 2552 ** ");
      startBackend(2552, "backend");
      //startWorker(0);
      System.out.println("** startWorker ** ");
      startWorker(2881);
      startWorker(2891);
      startWorker(2901);
      Thread.sleep(5000);
      //startFrontend(0);
      System.out.println("** startFrontend ** ");
      startFrontend(2441);
      startFrontend(2461);
    }
    else {
      int port = Integer.parseInt(args[0]);
      if (2000 <= port && port <= 2999)
        startBackend(port, "backend");
      else if (3000 <= port && port <= 3999)
        startFrontend(port);
      else
        startWorker(port);
    }
  }

  private static FiniteDuration workTimeout = Duration.create(10, "seconds");

  public static void startBackend(int port, String role) {
    Config conf = ConfigFactory.parseString("akka.cluster.roles=[" + role + "]").
        withFallback(ConfigFactory.parseString("akka.remote.netty.tcp.port=" + port)).
        withFallback(ConfigFactory.load());

    ActorSystem system = ActorSystem.create("ClusterSystem", conf);

    startupSharedJournal(system, (port == 2551),
        ActorPath$.MODULE$.fromString("akka.tcp://ClusterSystem@127.0.0.1:2551/user/store"));

    system.actorOf(ClusterSingletonManager.defaultProps(Master.props(workTimeout), "active",
        PoisonPill.getInstance(), role), "master");
  }

  public static void startWorker(int port) {
    Config conf = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + port).
        withFallback(ConfigFactory.load("worker"));

    ActorSystem system = ActorSystem.create("WorkerSystem", conf);

    Set<ActorSelection> initialContacts = new HashSet<ActorSelection>();
    for (String contactAddress : conf.getStringList("contact-points")) {
      initialContacts.add(system.actorSelection(contactAddress + "/user/receptionist"));
    }

    final ActorRef clusterClient = system.actorOf(ClusterClient.defaultProps(initialContacts), "clusterClient");
    system.actorOf(Worker.props(clusterClient, Props.create(WorkExecutor.class)), "worker");
  }

  public static void startFrontend(int port) {
    Config conf = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + port).
        withFallback(ConfigFactory.load());

    ActorSystem system = ActorSystem.create("ClusterSystem", conf);
    ActorRef frontend = system.actorOf(Props.create(Frontend.class), "frontend");
    system.actorOf(Props.create(WorkProducer.class, frontend), "producer");
    system.actorOf(Props.create(WorkResultConsumer.class), "consumer");
  }


  public static void  startupSharedJournal(final ActorSystem system, boolean startStore, final ActorPath path) {
    // Start the shared journal one one node (don't crash this SPOF)
    // This will not be needed with a distributed journal
    if (startStore) {
      system.actorOf(Props.create(SharedLeveldbStore.class), "store");
    }
    // register the shared journal

    Timeout timeout = new Timeout(15, TimeUnit.SECONDS );

    ActorSelection actorSelection = system.actorSelection(path);
    Future<Object> f = Patterns.ask(actorSelection, new Identify(null), timeout);

    f.onSuccess(new OnSuccess<Object>() {

      @Override
      public void onSuccess(Object arg0) throws Throwable {
        if (arg0 instanceof ActorIdentity && ((ActorIdentity) arg0).getRef() != null) {
          SharedLeveldbJournal.setStore(((ActorIdentity) arg0).getRef(), system);
        } else {
          System.err.println("Shared journal not started at "+ path);
          System.exit(-1);
        }

      }}, system.dispatcher());

    f.onFailure(new OnFailure() {
      public void onFailure(Throwable arg0) throws Throwable {
        System.err.println("Lookup of shared journal at "+path+" timed out" );
      }}, system.dispatcher());
  }
}
