package kz.sdp;
public record Route(String id, Location origin, Location destination, double distanceKm,
 int estimatedMinutes, double baseCost, double carbonEmission, double trafficMultiplier,
 boolean available, double additionalFees) {
 public Route {
  if(id==null || id.isBlank() || origin==null || destination==null || !Double.isFinite(distanceKm) || distanceKm<0 || estimatedMinutes<0 || !Double.isFinite(baseCost) || baseCost<0 || !Double.isFinite(carbonEmission) || carbonEmission<0 || !Double.isFinite(trafficMultiplier) || trafficMultiplier<1 || !Double.isFinite(additionalFees) || additionalFees<0) throw new IllegalArgumentException("Invalid route");
 }
}
