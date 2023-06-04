package demo.testing.v1;

import jdk.jfr.Description;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@SpringBootTest
public class FutureTest {


    @Test
    @Description("단일 쓰레드가 반복 호출되어 sleep * roomCount 만큼의 수행 시간이 소요된다.")
    public void singleThreadTest() {
        List<Map<Integer, String>> roomStatusList = new ArrayList<>();

        int roomCount = 10;
        for (int roomNo = 1; roomNo <= roomCount; roomNo++) {
            // Room 상태 조회(외부서비스) - Room 하나의 상태를 조회하는데 3초가 걸린다고 가정
            String roomStatus = this.getRoomStatus(roomNo);
            System.out.println("roomNo=" + roomNo + " " + "roomStatus=" + roomStatus);

            Map<Integer, String> statusMap = new HashMap<>();
            statusMap.put(roomNo, roomStatus);
            roomStatusList.add(statusMap);
        }

    }

    @Test
    @Description("newFixedThreadPool 방식으로 쓰레드 풀을 생성하고," +
            "멀티쓰레드 기반의 비동기 방식으로 룸 상태를 조회한다.")
    public void roomStatusTestByMultiThread() throws InterruptedException, ExecutionException {
        int roomCount = 10;

        List<Callable<Map<Integer, String>>> roomStatusJobs = new ArrayList<>();
        for (int roomNo = 1; roomNo <= roomCount; roomNo++) {
            roomStatusJobs.add(new RoomStatusTask(roomNo));
        }

        ExecutorService executorService = Executors.newFixedThreadPool(roomCount);
        List<Future<Map<Integer, String>>> resultList = executorService.invokeAll(roomStatusJobs);

        for (Future<Map<Integer, String>> futureMap : resultList) {
            futureMap.get().entrySet().forEach(entry -> {
                System.out.println("roomNo=" + entry.getKey() + " " + "roomStatus=" + entry.getValue());
            });
        }
    }

    /*
   Callable을 구현한 외부서비스 실행 타스크 클래스.
   ExecutorService의 invokeAll로 수행되는 멀티쓰레드 대상클래스
    */
    private class RoomStatusTask implements Callable<Map<Integer, String>> {
        private int roomNo;
        public RoomStatusTask(final int roomNo) {
            this.roomNo = roomNo;
        }
        @Override
        public Map<Integer, String> call() {

            Map<Integer, String> statusMap = new HashMap<>();
            // 서비스 호출
            String roomStatus = getRoomStatus(this.roomNo);

            statusMap.put(this.roomNo, roomStatus);
            return statusMap;
        }
    }


    private String getRoomStatus(int roomNo) {
        try {
            System.out.println("Called Room Status, RoomNo =" + roomNo);
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return roomNo + ": empty";
    }


}
