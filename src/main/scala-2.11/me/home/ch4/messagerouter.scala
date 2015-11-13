package me.home.ch4

import akka.actor.{Actor, ActorRef, Props}
import me.home.CompletableApp

object MessageRouterDriver extends CompletableApp(20) {
  val processor1 = system.actorOf(Props[Processor], "processor1")
  val processor2 = system.actorOf(Props[Processor], "processor2")

  val alternatingRouter = system.actorOf(Props(classOf[AlternatingRouter], processor1, processor2), "alternatingRouter")

  for (count <- 1 to 10) {
    alternatingRouter ! "Message #" + count
  }

  awaitCompletion

  println("MessageRouter: is completed")
  println("")
  println("")
  println("")
  println("")
}

class AlternatingRouter(processor1: ActorRef, processor2: ActorRef) extends Actor {
  var alternate = 1;

  def alternateProcessor() = {
    if (alternate == 1) {
      alternate = 2;
      processor1
    } else {
      alternate = 1
      processor2
    }
  }

  override def receive: Receive = {
    case message: Any =>
      val processor = alternateProcessor
      println(s"AlternatingRouter: routing $message to ${processor.path.name}")
      processor ! message
      MessageRouterDriver.completedStep()
  }
}

class Processor extends Actor {
  override def receive: Actor.Receive = {
    case message: Any =>
      println(s"Processor: ${self.path.name} received $message")
      MessageRouterDriver.completedStep()
  }
}