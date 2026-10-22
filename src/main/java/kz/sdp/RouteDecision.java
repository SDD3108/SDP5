package kz.sdp;
import java.util.List;
public record RouteDecision(String strategyName, Route selectedRoute, double score, String explanation, List<String> rejectedRoutes) {
 public RouteDecision { rejectedRoutes=List.copyOf(rejectedRoutes); }
}
