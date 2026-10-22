package kz.sdp;
import java.util.ArrayList;
import java.util.List;
public final class RouteRules {
 private RouteRules() {}
 public static double totalCost(Route route, DeliveryRequest request) {
  return route.baseCost()+75*route.distanceKm()+(request.priority()==DeliveryPriority.URGENT?500:0)+route.additionalFees();
 }
 public static double effectiveTime(Route route) { return route.estimatedMinutes()*route.trafficMultiplier(); }
 public static List<Route> eligible(List<Route> routes, DeliveryRequest request, List<String> rejected) {
  if(routes==null || request==null) throw new IllegalArgumentException("Routes and request are required");
  List<Route> result=new ArrayList<>();
  for(Route route:routes) {
   if(route==null) { rejected.add("null route: missing data"); continue; }
   String reason=null;
   if(!route.available()) reason="unavailable";
   else if(totalCost(route,request)>request.maxBudget()) reason="budget exceeded";
   else if(effectiveTime(route)>request.maxDeliveryMinutes()) reason="deadline exceeded";
   if(reason==null) result.add(route); else rejected.add(route.id()+": "+reason);
  }
  if(result.isEmpty()) throw new NoSuitableRouteException("No eligible routes: "+String.join("; ",rejected));
  return result;
 }
}
