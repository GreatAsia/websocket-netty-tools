package cn.creedon.client;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

public class Client {

    public static void main(String[] args) throws Exception {
        OptionSet options = getOptionSet(args);
        String host = (String) options.valueOf("host");
        Integer port = (Integer) options.valueOf("port");
        Integer size = (Integer) options.valueOf("size");
        EventLoopGroup group = new NioEventLoopGroup();
        for (int i = 0; i < size; i++) {
            ClientStartup clientStartup = new ClientStartup(group, host, port);
            clientStartup.startup();
        }
    }

    private static OptionSet getOptionSet(String[] args) throws Exception {
        final OptionParser optionParser = new OptionParser();
        optionParser.accepts("host").withRequiredArg()
                .describedAs("host").ofType(String.class);
        optionParser.accepts("port")
                .withOptionalArg()
                .describedAs("port").ofType(Integer.class);
        optionParser.accepts("size").withRequiredArg()
                .describedAs("size").ofType(Integer.class);
        OptionSet options = optionParser.parse(args);
        if(!options.has("host") || !options.has("port")) {
            throw new Exception("host/port is required");
        }
        if(!options.has("size")) {
            throw new Exception("size is required");
        }
        return options;
    }

}
