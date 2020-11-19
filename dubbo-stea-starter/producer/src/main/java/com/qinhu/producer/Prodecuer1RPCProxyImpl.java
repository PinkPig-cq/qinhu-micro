package com.qinhu.producer;

import com.qinhu.api.IProducer1RPCProxy;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @description:
 * @author: qh
 * @create: 2020-11-16 10:31
 **/
@DubboService(version = "1.0.1", tag = "producer1的代理")
public class Prodecuer1RPCProxyImpl implements IProducer1RPCProxy {

}
