package sky.terraberry;

import java.net.InetAddress;
import org.icmp4j.IcmpPingRequest;
import org.icmp4j.IcmpPingResponse;
import org.icmp4j.IcmpPingUtil;

public class IPFinder
{
    public static void main(String[] args)
    {
        try
        {
            for(int ip=100;ip<=200;ip++)
            {
                String ipAddress="192.168.32."+ip;
                IcmpPingRequest request=IcmpPingUtil.createIcmpPingRequest();
                request.setHost(ipAddress);
                request.setTimeout(2000L);
                IcmpPingResponse response=IcmpPingUtil.executePingRequest(request);
                boolean success=response.getSuccessFlag();
                System.out.print("ping "+ip+": "+success);
                if(success)
                {
                    InetAddress inet=InetAddress.getByName(ipAddress);
                    boolean reachable=inet.isReachable(5000);
                    System.out.print("    "+ip+(reachable?" is reachable":" is NOT reachable"));
                    if(reachable)
                        System.out.println(", name is:"+inet.getHostName());
                    else
                        System.out.println();
                }
                else
                    System.out.println();
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
