package kz.sdp;
import java.util.*;
public final class BalancedRouteStrategy implements RouteStrategy {
 private final double timeWeight,costWeight,distanceWeight,emissionWeight;
 public BalancedRouteStrategy(double timeWeight,double costWeight,double distanceWeight,double emissionWeight) {
  double[] weights={timeWeight,costWeight,distanceWeight,emissionWeight};
  for(double w:weights) if(!Double.isFinite(w)||w<0||w>1) throw new IllegalArgumentException("Invalid balanced weight");
  if(Math.abs(timeWeight+costWeight+distanceWeight+emissionWeight-1)>1e-9) throw new IllegalArgumentException("Weights must sum to 1");
  this.timeWeight=timeWeight; this.costWeight=costWeight; this.distanceWeight=distanceWeight; this.emissionWeight=emissionWeight;
 }
 private double normalize(double value,double min,double max) { return max==min?0:(value-min)/(max-min); }
 public RouteDecision selectRoute(List<Route> routes,DeliveryRequest request) {
  List<String> rejected=new ArrayList<>(); List<Route> eligible=RouteRules.eligible(routes,request,rejected);
  double minT=eligible.stream().mapToDouble(RouteRules::effectiveTime).min().orElseThrow(),maxT=eligible.stream().mapToDouble(RouteRules::effectiveTime).max().orElseThrow();
  double minC=eligible.stream().mapToDouble(r->RouteRules.totalCost(r,request)).min().orElseThrow(),maxC=eligible.stream().mapToDouble(r->RouteRules.totalCost(r,request)).max().orElseThrow();
  double minD=eligible.stream().mapToDouble(Route::distanceKm).min().orElseThrow(),maxD=eligible.stream().mapToDouble(Route::distanceKm).max().orElseThrow();
  double minE=eligible.stream().mapToDouble(Route::carbonEmission).min().orElseThrow(),maxE=eligible.stream().mapToDouble(Route::carbonEmission).max().orElseThrow();
  final double a=minT,b=maxT,c=minC,d=maxC,e=minD,f=maxD,g=minE,h=maxE;
  java.util.function.ToDoubleFunction<Route> score=r->timeWeight*normalize(RouteRules.effectiveTime(r),a,b)+costWeight*normalize(RouteRules.totalCost(r,request),c,d)+distanceWeight*normalize(r.distanceKm(),e,f)+emissionWeight*normalize(r.carbonEmission(),g,h);
  Route winner=eligible.stream().min(Comparator.comparingDouble(score).thenComparing(Route::id)).orElseThrow();
  return new RouteDecision(getStrategyName(),winner,score.applyAsDouble(winner),"Min-max normalized time/cost/distance/emissions; weights "+timeWeight+"/"+costWeight+"/"+distanceWeight+"/"+emissionWeight,rejected);
 }
 public String getStrategyName() { return "Balanced"; }
}
