package kz.sdp;
import java.util.*;
public final class DistanceFirstRouteStrategy implements RouteStrategy {
 public RouteDecision selectRoute(List<Route> routes,DeliveryRequest request) {
  List<String> rejected=new ArrayList<>();
  Route winner=RouteRules.eligible(routes,request,rejected).stream().min(Comparator.comparingDouble(Route::distanceKm).thenComparing(Route::id)).orElseThrow();
  return new RouteDecision(getStrategyName(),winner,winner.distanceKm(),"Minimum feasible distance: "+winner.distanceKm()+" km",rejected);
 }
 public String getStrategyName() { return "Distance first"; }
}
