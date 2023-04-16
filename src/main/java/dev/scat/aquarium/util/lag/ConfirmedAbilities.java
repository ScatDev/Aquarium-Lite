package dev.scat.aquarium.util.lag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

/**
 * @author Salers
 * made on gg.salers.anticheat.util.lag
 */
@Getter
@Setter
@AllArgsConstructor
public class ConfirmedAbilities {
    private boolean godMode, flying, flightAllowed, creativeMode;

    private float walkSpeed, flySpeed;

    public ConfirmedAbilities() {
        this(
                false,
                false,
                false,
                false,
                0.1F,
                0.05F
        );
    }
}