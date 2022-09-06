package com.genius.protocol;

import com.genius.config.Config;
import com.genius.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

import java.util.List;

@ChannelHandler.Sharable
public class MessageCodec extends MessageToMessageCodec<ByteBuf, Message> {

    private static final Byte VERSION = Config.getMessageVersion();
    private static final Serializer.Algorithm SERIALIZER = Config.getSerializer();
    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> outList) throws Exception {
        ByteBuf out = ctx.alloc().buffer();
        //choose com.genius.protocol version
        out.writeByte(VERSION);
        //choose serializer algorithm
        out.writeByte(SERIALIZER.ordinal());
        out.writeByte(msg.getMessageType());
        out.writeInt(msg.getSequenceId());
        out.writeByte(0xff);
        byte[] bytes = Config.getSerializer().serialize(msg);
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
        outList.add(out);

    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
       byte version = msg.readByte();
       byte serializer = msg.readByte();
       byte messageType = msg.readByte();
       int sequenceId = msg.readInt();
       msg.readByte();
       int length = msg.readInt();
       byte[] bytes = new byte[length];
       msg.readBytes(bytes,0,length);

       Serializer.Algorithm algorithm = Serializer.Algorithm.values()[serializer];
       Class<? extends Message> clazz = Message.getMessageClass(messageType);

        Message message = algorithm.deserialize(clazz, bytes);
        out.add(message);
    }
}
