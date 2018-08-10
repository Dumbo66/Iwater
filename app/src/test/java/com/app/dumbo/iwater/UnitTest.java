package com.app.dumbo.iwater;

import android.util.Log;

import com.jayway.jsonpath.JsonPath;

import org.junit.Test;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static android.support.constraint.Constraints.TAG;
import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class UnitTest {
    @Test
    public void test(){
        // 步骤1：创建被观察者 Observable & 发送事件
        // 在主线程创建被观察者 Observable 对象
        // 所以生产事件的线程是：主线程
        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                System.out.println(" 被观察者 Observable的工作线程是: " + Thread.currentThread().getName());
                // 打印验证
                emitter.onNext(1);
                emitter.onComplete();
            }
        });

        // 步骤2：创建观察者 Observer 并 定义响应事件行为
        // 在主线程创建观察者 Observer 对象
        // 所以接收 & 响应事件的线程是：主线程
        Observer<Integer> observer = new Observer<Integer>() {

            @Override
            public void onSubscribe(Disposable d) {
                System.out.println("开始采用subscribe连接");
                System.out.println(" 观察者 Observer的工作线程是: " + Thread.currentThread().getName());
                // 打印验证
            }
            @Override
            public void onNext(Integer value) {
                System.out.println("对Next事件"+ value +"作出响应" );
            }
            @Override
            public void onError(Throwable e) {
                System.out.println("对Error事件作出响应");
            }
            @Override
            public void onComplete() {
                System.out.println("对Complete事件作出响应");
            }
        };

        // 步骤3：通过订阅（subscribe）连接观察者和被观察者
        observable.subscribeOn(Schedulers.newThread()) // 1. 指定被观察者 生产事件的线程
                .observeOn(AndroidSchedulers.mainThread())  // 2. 指定观察者 接收 & 响应事件的线程
                .subscribe(observer); // 3. 最后再通过订阅（subscribe）连接观察者和被观察者

    }

    @Test
    public void test1(){
        String str="{\"status\":\"1\",\"info\":\"OK\",\"infocode\":\"10000\",\"regeocode\":{\"formatted_address\":\"天津市北辰区双口镇河北工业大学北辰校区东区二宿舍河北工业大学北辰校区\",\"addressComponent\":{\"country\":\"中国\",\"province\":\"天津市\",\"city\":[],\"citycode\":\"022\",\"district\":\"北辰区\",\"adcode\":\"120113\",\"township\":\"双口镇\",\"towncode\":\"120113103000\",\"neighborhood\":{\"name\":[],\"type\":[]},\"building\":{\"name\":[],\"type\":[]},\"streetNumber\":{\"street\":\"永保路\",\"number\":\"27号\",\"location\":\"117.071456,39.2439131\",\"direction\":\"东\",\"distance\":\"369.673\"},\"businessAreas\":[{\"location\":\"117.02976251630443,39.23107877173913\",\"name\":\"双口\",\"id\":\"120113\"}]}}}";
        System.out.println(JsonPath.read(str,"$.regeocode.formatted_address"));
    }
}