package cn.creedon.client.header;


import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * @author zhou
 * @date 2023/4/4
 */
public class Client {

    public static void main(String[] args)
            throws Exception
    {
        OptionSet options = getOptionSet(args);
        String host = (String)options.valueOf("host");
        Integer port = (Integer)options.valueOf("port");
        Integer size = (Integer)options.valueOf("size");
        String filePath = (String)options.valueOf("file");
        String ws = (String)options.valueOf("ws");
        EventLoopGroup group = new NioEventLoopGroup();
        if ((filePath != null) && (!"".equals(filePath)))
        {
            File file = new File(filePath);
            try
            {
                Scanner sc = new Scanner(file);
                Throwable localThrowable3 = null;
                try
                {
                    while (sc.hasNextLine())
                    {
                        String data = sc.nextLine();
                        if ((data != null) && (!"".equals(data)))
                        {
                            ClientStartup clientStartup = new ClientStartup(group, host, port,ws);
                            clientStartup.startup(data.trim());
                            Thread.sleep(20);
                        }
                    }
                }
                catch (Throwable localThrowable1)
                {
                    localThrowable3 = localThrowable1;throw localThrowable1;
                }
                finally
                {
                    if (sc != null) {
                        if (localThrowable3 != null) {
                            try
                            {
                                sc.close();
                            }
                            catch (Throwable localThrowable2)
                            {
                                localThrowable3.addSuppressed(localThrowable2);
                            }
                        } else {
                            sc.close();
                        }
                    }
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            for (int i = 0; i < ((size == null) || (size.intValue() < 1) ? 1 : size.intValue()); i++)
            {
                ClientStartup clientStartup = new ClientStartup(group, host, port, ws);
                clientStartup.startup();
            }
        }
    }

    private static OptionSet getOptionSet(String[] args)
            throws Exception
    {
        OptionParser optionParser = new OptionParser();
        optionParser.accepts("host").withRequiredArg().describedAs("host").ofType(String.class);
        optionParser.accepts("port").withRequiredArg().describedAs("port").ofType(Integer.class);
        optionParser.accepts("size").withOptionalArg().describedAs("size").ofType(Integer.class);
        optionParser.accepts("file").withOptionalArg().describedAs("file").ofType(String.class);
        optionParser.accepts("ws").withOptionalArg().describedAs("ws").ofType(String.class);
        OptionSet options = optionParser.parse(args);
        if ((!options.has("host")) || (!options.has("port"))) {
            throw new Exception("host/port is required");
        }
        return options;
    }


}
