package sample.blog

import akka.actor.{Props, ActorLogging, ActorRef, Actor}
import akka.cluster.sharding.ShardRegion
import akka.cluster.sharding.ShardRegion.ClusterShardingStats
import akka.util.Timeout
import scala.concurrent.duration._
import akka.pattern.ask

class SingletonActor(val shardRegion : ActorRef) extends Actor with ActorLogging {
  case object DoStuff

  implicit val ec = context.dispatcher
  implicit val timeout = Timeout(30.seconds)

  context.system.scheduler.schedule(5.seconds, 5.seconds, self, DoStuff)

  override def receive = {
    case DoStuff => {
      log.info("About to request stats")
      ask(shardRegion, ShardRegion.GetClusterShardingStats(10.seconds)).mapTo[ClusterShardingStats] map {
        stats => {
          log.info("Received stats: " + stats);
        }
      }
    }
  }
}

object SingletonActor {
  def name = "singletonactor"
  def props(shardRegion : ActorRef) = Props(new SingletonActor(shardRegion))
}