package kz.sdp;
import java.util.*;
public final class FastestRouteStrategy implements RouteStrategy {
 public RouteDecision selectRoute(List<Route> routes, DeliveryRequest request) {
  List<String> rejected=new ArrayList<>();
  Route winner=RouteRules.eligible(routes,request,rejected).stream().min(Comparator.comparingDouble(RouteRules::effectiveTime).thenComparing(Route::id)).orElseThrow();
  double score=RouteRules.effectiveTime(winner);
  return new RouteDecision(getStrategyName(),winner,score,"Estimated "+winner.estimatedMinutes()+" min x traffic "+winner.trafficMultiplier()+" = "+score+" min",rejected);
 }
 public String getStrategyName() { return "Fastest"; }
}
