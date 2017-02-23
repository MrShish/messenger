import static com.mrshish.messenger.json.JsonMapper.write;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.Test;

public class Tets {


    @Data
    @AllArgsConstructor
    public static class Channel {
        private UUID uuid;
        private String name;
        private String type;
    }



    @Test
    public void test() {

        List<Channel> channels = new ArrayList<>();

        String[] names = {"#pos", "Hello", "World", "Umut", "MrShish", "Andrea", "Anders", "Kofi", "Andrea, Umut", "Dan, Fred"};

        for(int i = 0; i < 10; i++) {
            channels.add(new Channel(UUID.randomUUID(), names[i], "CHANNEL"));
        }

        Map<UUID, Channel> channelsMap = channels.stream().collect(Collectors.toMap(Channel::getUuid, Function.identity()));
        System.out.println(write(channels));

    }
}
