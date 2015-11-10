package me.home.publishsubscribe

import akka.actor.{Props, Actor, ActorRef}
import akka.event.{SubchannelClassification, EventBus}
import akka.util.Subclassification
import me.home.CompletableApp
import me.home.publishsubscribe.Market

object SubClassificationDriver extends CompletableApp(6) {
  println("start application")
  val allSubscriber = system.actorOf(Props[AllMarketsSubscriber], "AllMarketsSubscriber")
  val nasdaqSubscriber = system.actorOf(Props[NASDAQSubscriber], "NASDAQSubscriber")
  val nyseSubscriber = system.actorOf(Props[NYSESubscriber], "NYSESubscriber")

  val quotesBus = new QuotesEventBus

  quotesBus.subscribe(allSubscriber, Market("quotes"))
  quotesBus.subscribe(nasdaqSubscriber, Market("quotes/NASDAQ"))
  quotesBus.subscribe(nyseSubscriber, Market("quotes/NYSE"))

  quotesBus.publish(PriceQuoted(Market("quotes/NYSE"), Symbol("ORCL"), new Money("37.84")))
  quotesBus.publish(PriceQuoted(Market("quotes/NASDAQ"), Symbol("MSFT"), new Money("37.16")))
//  quotesBus.publish(PriceQuoted(Market("quotes/DAX"), Symbol("SAP:GR"), new Money("61.95")))
//  quotesBus.publish(PriceQuoted(Market("quotes/NKY"), Symbol("6701:JP"), new Money("237")))

  awaitCompletion

}

case class Money(amount: BigDecimal) {
  def this(amount: String) = this(new java.math.BigDecimal(amount))

  amount.setScale(4, BigDecimal.RoundingMode.HALF_UP)
}

case class Market(name: String)

case class PriceQuoted(market: Market, ticker: Symbol, price: Money)

class QuotesEventBus extends EventBus with SubchannelClassification {

  type Classifier = Market
  type Event = PriceQuoted
  type Subscriber = ActorRef

  protected def classify(event: Event): Classifier = {
    event.market
  }

  protected def publish(event: Event, subscriber: Subscriber): Unit = {
    println("subscriber {}", subscriber)
    subscriber ! event
  }

  protected def subclassification = new Subclassification[Classifier] {
    def isEqual(
                 subscribedToClassifier: Classifier,
                 eventClassifier: Classifier): Boolean = {

      subscribedToClassifier.equals(eventClassifier)
    }

    def isSubclass(
                    subscribedToClassifier: Classifier,
                    eventClassifier: Classifier): Boolean = {

      subscribedToClassifier.name.startsWith(eventClassifier.name)
    }
  }
}

class AllMarketsSubscriber extends Actor {
  def receive = {
    case quote: PriceQuoted =>
      println(s"AllMarketsSubscriber received: $quote")
      SubClassificationDriver.completedStep
  }
}

class NASDAQSubscriber extends Actor {
  def receive = {
    case quote: PriceQuoted =>
      println(s"NASDAQSubscriber received: $quote")
      SubClassificationDriver.completedStep
  }
}

class NYSESubscriber extends Actor {
  def receive = {
    case quote: PriceQuoted =>
      println(s"NYSESubscriber received: $quote")
      SubClassificationDriver.completedStep
  }
}
