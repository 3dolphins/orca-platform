package im.actor.core.api.rpc;
/*
 *  Generated by the Actor API Scheme generator.  DO NOT EDIT!
 */

import im.actor.runtime.bser.*;
import im.actor.runtime.collections.*;
import static im.actor.runtime.bser.Utils.*;
import im.actor.core.network.parser.*;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;
import com.google.j2objc.annotations.ObjectiveCName;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import im.actor.core.api.*;

public class ResponseCommitFileUpload extends Response {

    public static final int HEADER = 0x8a;
    public static ResponseCommitFileUpload fromBytes(byte[] data) throws IOException {
        return Bser.parse(new ResponseCommitFileUpload(), data);
    }

    private ApiFileLocation uploadedFileLocation;

    public ResponseCommitFileUpload(@NotNull ApiFileLocation uploadedFileLocation) {
        this.uploadedFileLocation = uploadedFileLocation;
    }

    public ResponseCommitFileUpload() {

    }

    @NotNull
    public ApiFileLocation getUploadedFileLocation() {
        return this.uploadedFileLocation;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.uploadedFileLocation = values.getObj(1, new ApiFileLocation());
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.uploadedFileLocation == null) {
            throw new IOException();
        }
        writer.writeObject(1, this.uploadedFileLocation);
    }

    @Override
    public String toString() {
        String res = "tuple CommitFileUpload{";
        res += "}";
        return res;
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}