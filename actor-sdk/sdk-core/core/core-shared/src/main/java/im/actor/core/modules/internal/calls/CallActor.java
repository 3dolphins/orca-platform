package im.actor.core.modules.internal.calls;

import java.util.ArrayList;

import im.actor.core.modules.ModuleContext;
import im.actor.core.modules.internal.CallsModule;
import im.actor.core.modules.utils.ModuleActor;
import im.actor.runtime.Log;
import im.actor.runtime.actors.messages.PoisonPill;

public class CallActor extends ModuleActor {

    public static final String TAG = "CallActor";
    private boolean inited = false;
    private int timeout = 0;
    private boolean alive = false;
    private long callId;
    private CallsModule.CallCallback callback;
    private ArrayList<byte[]> signals = new ArrayList<byte[]>();

    public CallActor(long callId, ModuleContext context) {
        super(context);
        this.callId = callId;
    }

    public CallActor(long callId, CallsModule.CallCallback callCallback, ModuleContext context) {
        super(context);
        this.callId = callId;
        this.callback = callCallback;
    }

    @Override
    public void preStart() {
        super.preStart();
        self().send(new SendCallInProgress());
    }

    @Override
    public void onReceive(Object message) {
        if (message instanceof CallInProgress) {
            onCallInProgress(((CallInProgress) message).getTimeout());
        } else if (message instanceof CheckAlive) {
            checkAlive();
        } else if (message instanceof SendCallInProgress) {
            sendCallInProgress();
        } else if (message instanceof EndCall) {
            onEndCall();
        } else if (message instanceof Signal) {
            onSignal(((Signal) message).getData());
        } else if (message instanceof AnswerCall) {
            onAnswerCall(((AnswerCall) message).getCallback());
        }
    }

    public void onSignal(byte[] data) {
        if (callback != null) {
            callback.onSignal(data);
        } else {
            signals.add(data);
        }
    }

    public void onEndCall() {
        if (callback == null) {
            self().send(new EndCall(), 500);
            return;
        }
        callback.onCallEnd();
        context().getCallsModule().onCallEnded(callId);
        self().send(PoisonPill.INSTANCE);
    }

    public void checkAlive() {
        if (alive) {
            alive = false;
            self().send(new CheckAlive(), timeout * 1000);
        } else {
            Log.d(TAG, "no call in progress - call is dead");
            context().getCallsModule().endCall(callId);
            self().send(PoisonPill.INSTANCE);
        }
    }

    private void sendCallInProgress() {
        context().getCallsModule().callInProgress(callId);
        self().send(new SendCallInProgress(), CallsModule.CALL_TIMEOUT * 1000 / 3);

    }

    private void onCallInProgress(int timeout) {
        alive = true;
        this.timeout = timeout;
        if (!inited) {
            inited = true;
            alive = false;
            self().send(new CheckAlive(), timeout * 1000);
        }
    }

    private void onAnswerCall(CallsModule.CallCallback callCallback) {
        this.callback = callCallback;
        for (byte[] s : signals) {
            callback.onSignal(s);
        }
    }

    public static class EndCall {

    }

    public static class Signal {
        byte[] data;

        public Signal(byte[] data) {
            this.data = data;
        }

        public byte[] getData() {
            return data;
        }
    }

    public static class CallInProgress {

        int timeout;

        public CallInProgress(int timeout) {
            this.timeout = timeout;
        }

        public int getTimeout() {
            return timeout;
        }

    }

    public static class AnswerCall {
        CallsModule.CallCallback callback;

        public AnswerCall(CallsModule.CallCallback callback) {
            this.callback = callback;
        }

        public CallsModule.CallCallback getCallback() {
            return callback;
        }
    }

    private static class CheckAlive {

    }

    private static class SendCallInProgress {

    }
}