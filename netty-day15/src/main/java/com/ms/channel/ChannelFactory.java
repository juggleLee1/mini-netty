package com.ms.channel;



public interface ChannelFactory<T extends Channel> {


    T newChannel();
}
