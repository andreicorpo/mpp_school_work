package networking.server;

import model.participant.IParticipant;
import model.participant.ParticipantTeam;
import model.staff.Staff;
import utils.ErrorType;
import utils.RacePerson;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ServerRpcProxy implements IServer {
    private String host;
    private int port;

    private IClientObserver client;

    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Socket connection;

    private BlockingQueue<Response> queueResponses;

    private volatile boolean finished;

    public ServerRpcProxy(String host, int port) {
        this.host = host;
        this.port = port;
        queueResponses = new LinkedBlockingQueue<>();
    }

    @Override
    public void login(String userAndPass, IClientObserver client) {
        initializeConnection();
        Request request = new Request.Builder().type(RequestType.LOGIN).data(userAndPass).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type() == ResponseType.STAFF) {
            this.client = client;
            this.client.setStaff((Staff) response.data());
        } else if (response.type() == ResponseType.ERROR) {
            ErrorType errorType = (ErrorType) response.data();
            if (errorType != null) {
                client.error(errorType);
            }
        } else {
            System.out.println(response.data().toString());
        }
    }

    private Response readResponse() {
        Response response = null;
        try {
            response = queueResponses.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return response;
    }

    @Override
    public void logout(Staff staff, IClientObserver client) {
        Request request = new Request.Builder().type(RequestType.LOGOUT).data(staff).build();
        sendRequest(request);
        closeConnection();
    }

    @Override
    public void updateParticipants() {
        Request request = new Request.Builder().type(RequestType.UPDATE).build();
        sendRequest(request);
    }

    @Override
    public void numOfParticipants() {
        Request request = new Request.Builder().type(RequestType.PARTICIPANTS).build();
        sendRequest(request);
    }

    @Override
    public void getRaces(IClientObserver client) {
        Request request = new Request.Builder().type(RequestType.RACES).build();
        sendRequest(request);
    }

    @Override
    public void searchByTeam(String teamName, IClientObserver client) {
        Request request = new Request.Builder().type(RequestType.SEARCH).data(teamName).build();
        sendRequest(request);
    }

    @Override
    public void addParticipant(IParticipant participant) {
        Request request = new Request.Builder().type(RequestType.ADD).data(participant).build();
        sendRequest(request);
    }

    @Override
    public void checkStaff(String userAndPass, IClientObserver clientObserver) {
        System.out.println(userAndPass);
        System.out.println(clientObserver == null ? "client not ok" : "client ok");
        Request request = new Request.Builder().type(RequestType.CHECK_STAFF).data(userAndPass).build();
        sendRequest(request);
    }

    private void initializeConnection() {
        try {
            connection = new Socket(host, port);
            out = new ObjectOutputStream(connection.getOutputStream());
            out.flush();
            in = new ObjectInputStream(connection.getInputStream());
            finished = false;
            startReader();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeConnection() {
        finished = true;
        try {
            in.close();
            out.close();
            connection.close();
            client = null;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void sendRequest(Request request) {
        try {
            System.out.println("Sending Request: " + request + " " + LocalDateTime.now());
            out.writeObject(request);
            out.flush();
        } catch (IOException e) {
            System.out.println("Error sending object " + e);
        }

    }

    private void startReader() {
        Thread tw = new Thread(new ReaderThread());
        tw.start();
    }

    private void handleUpdate(Response response) {
        if (response.type() == ResponseType.UPDATE) {
            ArrayList<RacePerson> data = (ArrayList<RacePerson>) response.data();
            if (data != null) {
                client.updateParticipantsNum(data);
            }
        }
        if (response.type() == ResponseType.SEARCH) {
            ArrayList<ParticipantTeam> data = (ArrayList<ParticipantTeam>) response.data();
            if (data != null) {
                client.participantsByTeam(data);
            }
        }
        if (response.type() == ResponseType.RACES) {
            ArrayList<String> data = (ArrayList<String>) response.data();
            if (data != null) {
                client.currentRaces(data);
            }
        }
        if (response.type() == ResponseType.PARTICIPANTS) {
            Integer data = (Integer) response.data();
            if (data != null) {
                client.numOfParticipants(data);
            }
        }


    }

    private boolean isUpdate(Response response) {
        return response.type() == ResponseType.UPDATE ||
                response.type() == ResponseType.SEARCH ||
                response.type() == ResponseType.RACES ||
                response.type() == ResponseType.PARTICIPANTS;
    }

    private class ReaderThread implements Runnable {
        public void run() {
            while (!finished) {
                try {
                    Object response = in.readObject();
                    Response res = (Response) response;
                    System.out.println("response received " + res + " " + LocalDateTime.now());
                    if (isUpdate((Response) response)) {
                        handleUpdate((Response) response);
                    } else {

                        try {
                            queueResponses.put((Response) response);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Logged out");
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (ClassCastException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
