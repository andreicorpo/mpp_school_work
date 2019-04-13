package networking.server;

import java.net.Socket;

public class RpcConcurrentServer extends AbstractConcurrentServer {

    private IServer server;

    public RpcConcurrentServer(int port, IServer server) {
        super(port);
        this.server = server;
    }

    @Override
    protected Thread createWorker(Socket client) {
        ClientReflectionWorker worker = new ClientReflectionWorker(server, client);
        return new Thread(worker);
    }
}
