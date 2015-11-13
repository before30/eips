package me.home.ch5

import akka.actor.{Actor, Props}
import me.home.CompletableApp

object PointToPointChannelDriver extends CompletableApp(4) {
  val actorB = system.actorOf(Props[ActorB])

  actorB ! "Goodbye, from actor C!"
  actorB ! "Hello, from actor A!"
  actorB ! "Goodbye again, from actor C!"
  actorB ! "Hello again, from actor A!"

  awaitCompletion

  println("PointToPointChannel: Completed.")
}

class ActorB extends Actor {
  var goodbye = 0
  var goodbyeAgain = 0
  var hello = 0
  var helloAgain = 0

  override def receive: Receive = {
    case message: String =>
      hello = hello +
        (if (message.contains("Hello")) 1 else 0)
      helloAgain = helloAgain +
        (if (message.startsWith("Hello again")) 1 else 0)
      assert(hello == 0 || hello > helloAgain)

      goodbye = goodbye +
        (if (message.contains("Goodbye")) 1 else 0)
      goodbyeAgain = goodbyeAgain +
        (if (message.startsWith("Goodbye again")) 1 else 0)
      assert(goodbye == 0 || goodbye > goodbyeAgain)

      println(s"$hello , $helloAgain , $goodbye , $goodbyeAgain")

      PointToPointChannelDriver.completedStep
  }
}