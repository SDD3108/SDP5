package kz.sdp;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
public final class StrategySelector {
 private final Map<DeliveryPriority,RouteStrategy> strategies=new EnumMap<>(DeliveryPriority.class);
 public StrategySelector() {
  register(DeliveryPriority.URGENT,new FastestRouteStrategy());
  register(DeliveryPriority.ECONOMY,new CheapestRouteStrategy());
  register(DeliveryPriority.ECO,new EcoFriendlyRouteStrategy());
  register(DeliveryPriority.STANDARD,new BalancedRouteStrategy(.4,.3,.1,.2));
 }
 public void register(DeliveryPriority priority,RouteStrategy strategy) { strategies.put(Objects.requireNonNull(priority),Objects.requireNonNull(strategy)); }
 public RouteStrategy select(DeliveryRequest request) {
  Objects.requireNonNull(request);
  if(request.ecoPreferred() && request.priority()==DeliveryPriority.STANDARD) return strategies.get(DeliveryPriority.ECO);
  return Objects.requireNonNull(strategies.get(request.priority()),"No strategy registered");
 }
}
