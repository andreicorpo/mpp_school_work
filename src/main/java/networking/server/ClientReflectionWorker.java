package networking.server;

import model.participant.IParticipant;
import model.participant.ParticipantTeam;
import model.staff.Staff;
import utils.ErrorType;
import utils.RacePerson;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.ArrayList;

public class ClientReflectionWorker implements Runnable, IClientObserver {

    private static Response okResponse = new Response.Builder().type(ResponseType.OK).build();
    private IServer server;
    private Socket connection;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private volatile boolean connected;

    public ClientReflectionWorker(IServer server, Socket connection) {
        this.server = server;
        this.connection = connection;
        try {
            out = new ObjectOutputStream(connection.getOutputStream());
            out.flush();
            in = new ObjectInputStream(connection.getInputStream());
            connected = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (connected) {
            try {
                Object request = in.readObject();
                Response response = handleRequest((Request) request);
                if (response != null) {
                    if (!hasData(response)) {
                        sendResponse(response);
                    }
                }
            } catch (IOException e) {
                System.out.println("Client closed.");
                connected = false;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            in.close();
            out.close();
            connection.close();
        } catch (IOException e) {
            System.out.println("Client logged out");
        }
    }

    public boolean hasData(Response response) {
        return response.type() == ResponseType.UPDATE ||
                response.type() == ResponseType.SEARCH ||
                response.type() == ResponseType.RACES ||
                response.type() == ResponseType.PARTICIPANTS ||
                response.type() == ResponseType.STAFF;
    }

    private void sendResponse(Response response) throws IOException {
        System.out.println("Sending Response " + response);
        out.writeObject(response);
        out.flush();
    }

    private Response handleRequest(Request request) {
        Response response = null;
        String handlerName = "handle" + (request).type();
        System.out.println("HandlerName " + handlerName);
        try {
            Method method = this.getClass().getDeclaredMethod(handlerName, Request.class);
            response = (Response) method.invoke(this, request);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return response;
    }

    private Response handleLOGIN(Request request) {
        String userAndPass = (String) request.data();
        server.login(userAndPass, this);
        return new Response.Builder().type(ResponseType.STAFF).build();
    }

    private Response handleLOGOUT(Request request) {
        Staff staff = (Staff) request.data();
        server.logout(staff, this);
        return okResponse;
    }

    private Response handleADD(Request request) {
        IParticipant participant = (IParticipant) request.data();
        server.addParticipant(participant);
        return okResponse;
    }

    private Response handleUPDATE(Request request) {
        server.updateParticipants();
        return new Response.Builder().type(ResponseType.UPDATE).build();
    }

    private Response handleSEARCH(Request request) {
        server.searchByTeam((String) request.data(), this);
        return new Response.Builder().type(ResponseType.SEARCH).build();
    }

    private Response handleRACES(Request request) {
        server.getRaces(this);
        return new Response.Builder().type(ResponseType.SEARCH).build();
    }

    private Response handlePARTICIPANTS(Request request) {
        server.numOfParticipants();
        return new Response.Builder().type(ResponseType.PARTICIPANTS).build();
    }

    private Response handleCHECK_STAFF(Request request) {
        server.checkStaff((String) request.data(), this);
        return new Response.Builder().type(ResponseType.STAFF).build();
    }

    @Override
    public void updateParticipantsNum(ArrayList<RacePerson> data) {
        Response response = new Response.Builder().type(ResponseType.UPDATE).data(data).build();
        try {
            sendResponse(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void participantsByTeam(ArrayList<ParticipantTeam> data) {
        Response response = new Response.Builder().type(ResponseType.SEARCH).data(data).build();
        try {
            sendResponse(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void currentRaces(ArrayList<String> data) {
        Response response = new Response.Builder().type(ResponseType.RACES).data(data).build();
        try {
            sendResponse(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void numOfParticipants(Integer num) {
        Response response = new Response.Builder().type(ResponseType.PARTICIPANTS).data(num).build();
        try {
            sendResponse(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setStaff(Staff staff) {
        Response response = new Response.Builder().type(ResponseType.STAFF).data(staff).build();
        try {
            sendResponse(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void error(ErrorType errorType) {
        Response response = new Response.Builder().type(ResponseType.ERROR).data(errorType).build();
        try {
            sendResponse(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
