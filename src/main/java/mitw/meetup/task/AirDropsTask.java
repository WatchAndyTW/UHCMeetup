package mitw.meetup.task;

import mitw.meetup.UHCMeetup;
import mitw.meetup.scenarios.AirDrops;
import org.bukkit.scheduler.BukkitRunnable;

public class AirDropsTask extends BukkitRunnable {

    private int time;

    public AirDropsTask() {
        time = 120;
        AirDrops.getInstance().generateNewLocation();
        AirDrops.getInstance().dropChest();
        AirDrops.getInstance().AnnounceDrops(time + "", true);
        AirDrops.getInstance().generateNewLocation();
    }

    @Override
    public void run() {
        if (time == 60 || time == 30 || time == 15 || time == 10 || time <= 5 && time > 0) {
            AirDrops.getInstance().AnnounceDrops(time + "", false);
        } else if (time == 0) {
            time = 120;
            AirDrops.getInstance().AnnounceDrops(time + "", true);
            AirDrops.getInstance().dropChest();
            AirDrops.getInstance().generateNewLocation();
        }
        time--;
    }

    public static void init() {
        new AirDropsTask().runTaskTimer(UHCMeetup.getInstance(), 20, 20l);
    }

}
