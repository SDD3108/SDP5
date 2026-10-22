package kz.sdp;
import java.util.*;
public final class CheapestRouteStrategy implements RouteStrategy {
 public RouteDecision selectRoute(List<Route> routes, DeliveryRequest request) {
  List<String> rejected=new ArrayList<>();
  Route winner=RouteRules.eligible(routes,request,rejected).stream().min(Comparator.comparingDouble((Route r)->RouteRules.totalCost(r,request)).thenComparing(Route::id)).orElseThrow();
  double score=RouteRules.totalCost(winner,request);
  return new RouteDecision(getStrategyName(),winner,score,"Base "+winner.baseCost()+" + 75 x "+winner.distanceKm()+" km + priority "+(request.priority()==DeliveryPriority.URGENT?500:0)+" + fees "+winner.additionalFees()+" = "+score,rejected);
 }
 public String getStrategyName() { return "Cheapest"; }
}
