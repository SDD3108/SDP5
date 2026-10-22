package kz.sdp;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class RouteEngineTest {
 private final Location a=new Location("A",51,71), b=new Location("B",52,72);
 private final Route fast=new Route("FAST",a,b,15,10,2000,5,1.2,true,0);
 private final Route cheap=new Route("CHEAP",a,b,5,40,300,2,1.0,true,0);
 private final Route green=new Route("GREEN",a,b,8,30,900,.1,1.0,true,0);
 private final Route closed=new Route("CLOSED",a,b,1,1,1,0,1,false,0);
 private final List<Route> routes=List.of(fast,cheap,green,closed);
 private final DeliveryRequest request=new DeliveryRequest(DeliveryPriority.STANDARD,10000,100,false);

 @Test void fastestAccountsForTraffic() { assertEquals("FAST",new FastestRouteStrategy().selectRoute(routes,request).selectedRoute().id()); }
 @Test void cheapestUsesTotalCost() { assertEquals("CHEAP",new CheapestRouteStrategy().selectRoute(routes,request).selectedRoute().id()); }
 @Test void ecoUsesEmissionAndDistance() { assertEquals("GREEN",new EcoFriendlyRouteStrategy().selectRoute(routes,request).selectedRoute().id()); }
 @Test void balancedReturnsFeasibleRoute() { assertTrue(routes.contains(new BalancedRouteStrategy(.4,.3,.1,.2).selectRoute(routes,request).selectedRoute())); }
 @Test void invalidWeightsRejected() { assertThrows(IllegalArgumentException.class,()->new BalancedRouteStrategy(.4,.4,.4,.4)); }
 @Test void negativeWeightsRejected() { assertThrows(IllegalArgumentException.class,()->new BalancedRouteStrategy(-.1,.5,.3,.3)); }
 @Test void runtimeSwitchChangesChoice() {
  RoutePlanner planner=new RoutePlanner(new FastestRouteStrategy());
  assertEquals("FAST",planner.plan(routes,request).selectedRoute().id());
  planner.setStrategy(new CheapestRouteStrategy());
  assertEquals("CHEAP",planner.plan(routes,request).selectedRoute().id());
 }
 @Test void selectorMapsPriority() {
  StrategySelector selector=new StrategySelector();
  assertInstanceOf(FastestRouteStrategy.class,selector.select(new DeliveryRequest(DeliveryPriority.URGENT,10000,100,false)));
  assertInstanceOf(CheapestRouteStrategy.class,selector.select(new DeliveryRequest(DeliveryPriority.ECONOMY,10000,100,false)));
  assertInstanceOf(EcoFriendlyRouteStrategy.class,selector.select(new DeliveryRequest(DeliveryPriority.ECO,10000,100,true)));
  assertInstanceOf(BalancedRouteStrategy.class,selector.select(request));
 }
 @Test void ecoPreferenceOverridesStandard() { assertInstanceOf(EcoFriendlyRouteStrategy.class,new StrategySelector().select(new DeliveryRequest(DeliveryPriority.STANDARD,10000,100,true))); }
 @Test void hardConstraintsRejectUnavailable() { assertTrue(new FastestRouteStrategy().selectRoute(routes,request).rejectedRoutes().stream().anyMatch(x->x.contains("CLOSED"))); }
 @Test void budgetFiltersRoutes() {
  DeliveryRequest limited=new DeliveryRequest(DeliveryPriority.STANDARD,600,100,false);
  assertThrows(NoSuitableRouteException.class,()->new CheapestRouteStrategy().selectRoute(routes,limited));
 }
 @Test void deadlineFiltersRoutes() {
  DeliveryRequest limited=new DeliveryRequest(DeliveryPriority.STANDARD,10000,15,false);
  assertEquals("FAST",new CheapestRouteStrategy().selectRoute(routes,limited).selectedRoute().id());
 }
 @Test void noValidRouteThrows() { assertThrows(NoSuitableRouteException.class,()->new FastestRouteStrategy().selectRoute(List.of(closed),request)); }
 @Test void emptyListThrows() { assertThrows(NoSuitableRouteException.class,()->new FastestRouteStrategy().selectRoute(List.of(),request)); }
 @Test void fifthStrategyChoosesShortest() { assertEquals("CHEAP",new DistanceFirstRouteStrategy().selectRoute(routes,request).selectedRoute().id()); }
 @Test void differentStrategiesProduceDifferentResults() {
  assertNotEquals(new FastestRouteStrategy().selectRoute(routes,request).selectedRoute(),new CheapestRouteStrategy().selectRoute(routes,request).selectedRoute());
 }
 @Test void exactBudgetBoundaryAccepted() {
  double exact=RouteRules.totalCost(cheap,request);
  DeliveryRequest boundary=new DeliveryRequest(DeliveryPriority.STANDARD,exact,100,false);
  assertEquals("CHEAP",new CheapestRouteStrategy().selectRoute(routes,boundary).selectedRoute().id());
 }
 @Test void invalidRouteDataRejected() { assertThrows(IllegalArgumentException.class,()->new Route("BAD",a,b,-1,10,100,1,1,true,0)); }
 @Test void missingRequestRejected() { assertThrows(IllegalArgumentException.class,()->new FastestRouteStrategy().selectRoute(routes,null)); }
 @Test void routePlannerRejectsNullStrategy() { assertThrows(NullPointerException.class,()->new RoutePlanner(null)); }
 @Test void explanationContainsCostFormula() { assertTrue(new CheapestRouteStrategy().selectRoute(routes,request).explanation().contains("75 x")); }
}
