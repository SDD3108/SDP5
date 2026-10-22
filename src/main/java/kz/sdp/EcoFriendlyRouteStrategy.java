package kz.sdp;
import java.util.*;
public final class EcoFriendlyRouteStrategy implements RouteStrategy {
 private double score(Route r) { return 0.8*r.carbonEmission()+0.2*r.distanceKm(); }
 public RouteDecision selectRoute(List<Route> routes, DeliveryRequest request) {
  List<String> rejected=new ArrayList<>();
  Route winner=RouteRules.eligible(routes,request,rejected).stream().min(Comparator.comparingDouble(this::score).thenComparing(Route::id)).orElseThrow();
  return new RouteDecision(getStrategyName(),winner,score(winner),"0.8 x CO2 "+winner.carbonEmission()+" + 0.2 x distance "+winner.distanceKm(),rejected);
 }
 public String getStrategyName() { return "Eco friendly"; }
}
