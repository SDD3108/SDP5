package kz.sdp;
import java.util.Objects;
public record DeliveryRequest(DeliveryPriority priority, double maxBudget, int maxDeliveryMinutes, boolean ecoPreferred) {
 public DeliveryRequest {
  Objects.requireNonNull(priority, "priority");
  if(Double.isNaN(maxBudget) || maxBudget<0 || maxDeliveryMinutes<0) throw new IllegalArgumentException("Invalid constraints");
 }
}
