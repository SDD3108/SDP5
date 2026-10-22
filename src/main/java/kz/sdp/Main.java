package kz.sdp;
import java.util.*;
public final class Main {
 public static void main(String[] args) {
  Location depot=new Location("Astana Depot",51.1694,71.4491),customer=new Location("City Hospital",51.1333,71.4333);
  List<Route> routes=List.of(
   new Route("R1",depot,customer,9,24,1500,3.2,1.6,true,100),
   new Route("R2",depot,customer,13,18,2700,4.5,1.1,true,50),
   new Route("R3",depot,customer,7,37,900,1.4,1.1,true,0),
   new Route("R4",depot,customer,11,29,1250,0.8,1.2,true,150),
   new Route("R5",depot,customer,16,21,2100,2.1,1.3,true,100),
   new Route("R6",depot,customer,6,48,700,0.5,1.1,true,0),
   new Route("R7",depot,customer,10,19,2000,2.8,1.1,false,0),
   new Route("R8",depot,customer,8,30,1100,1.0,1.2,true,100));
  RoutePlanner planner=new RoutePlanner(new FastestRouteStrategy());
  StrategySelector selector=new StrategySelector();
  List<DeliveryRequest> requests=List.of(
   new DeliveryRequest(DeliveryPriority.URGENT,10000,60,false),
   new DeliveryRequest(DeliveryPriority.ECONOMY,4000,90,false),
   new DeliveryRequest(DeliveryPriority.ECO,8000,90,true),
   new DeliveryRequest(DeliveryPriority.STANDARD,8000,90,false));
  for(DeliveryRequest request:requests) {
   planner.setStrategy(selector.select(request));
   RouteDecision decision=planner.plan(routes,request);
   System.out.println("AUTO "+request.priority()+" -> "+decision.strategyName()+" -> "+decision.selectedRoute().id());
  }
  DeliveryRequest compare=requests.get(3);
  System.out.println("\nSTRATEGY COMPARISON (same request)");
  List<RouteStrategy> all=List.of(new FastestRouteStrategy(),new CheapestRouteStrategy(),new EcoFriendlyRouteStrategy(),new BalancedRouteStrategy(.4,.3,.1,.2),new DistanceFirstRouteStrategy());
  for(RouteStrategy strategy:all) {
   planner.setStrategy(strategy);
   RouteDecision decision=planner.plan(routes,compare);
   System.out.printf("%s: %s | score %.3f | time %.1f min | cost %.0f | CO2 %.1f kg%n",decision.strategyName(),decision.selectedRoute().id(),decision.score(),RouteRules.effectiveTime(decision.selectedRoute()),RouteRules.totalCost(decision.selectedRoute(),compare),decision.selectedRoute().carbonEmission());
   System.out.println(" Reason: "+decision.explanation());
   decision.rejectedRoutes().forEach(reason->System.out.println(" Rejected: "+reason));
  }
 }
}
