package pl.north93.zgame.api.global.redis.messaging.impl;

/*default*/ interface InstanceCreator<T>
{
    T newInstance();
}
