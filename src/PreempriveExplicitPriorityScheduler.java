import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class PreempriveExplicitPriorityScheduler extends Scheduler {
    private long ageFactor;

    public PreempriveExplicitPriorityScheduler(ArrayList<Process> processes, long ageFactor) {
        super(processes);
        this.ageFactor = ageFactor;
    }

    Record findRecordByPID(ArrayList<Record> records, long PID){
        for(Record record : records){
            if(record.getProcessID() == PID) return record;
        }
        return null;
    }

    @Override
    public void schedule() {
        // Sort The processes by arrival time
        Collections.sort(this.processes);
        // Set a cursor to traverse the processes
        int cursor = 0;
        // Number of finished processes
        int finished = 0;
        // Clearing the Ages and Remaining Times for All Procesess
        for(Process process : this.processes){
            process.setAge(0);
            process.setRemainingTime(process.getTaskDuration());
        }
        // Set The Age Factor (How much does the aging affects priority)
        Process.ageFactor = ageFactor;
        // While there are processes to execute
        while(finished < this.processes.size()){
            // Add new arrival processes to ready queue (depending on their burst)
            while(this.processes.size() > cursor && this.processes.get(cursor).getArrivalTime() <= this.currentTime){
                // Insert
                this.readyQueue.add(this.processes.get(cursor));
                this.processesLog.add(new Record(
                        this.processes.get(cursor).getProcessID(),
                        Long.MAX_VALUE,
                        Long.MIN_VALUE,
                        this.processes.get(cursor).getTaskDuration(),
                        this.processes.get(cursor).getArrivalTime()
                ));
                cursor++;
            }
            // Sort
            Collections.sort(this.readyQueue, Comparator.comparingLong(Process::getPriority));
            if(!readyQueue.isEmpty()) {
                // Making progress in the process
                this.readyQueue.get(0).setRemainingTime(this.readyQueue.get(0).getRemainingTime() - 1);
                Record record = findRecordByPID(this.processesLog, this.readyQueue.get(0).getProcessID());
                record.setStartTime(Long.min(currentTime, record.getStartTime()));
                record.setFinishTime(Long.max(currentTime, record.getFinishTime()));
                // Record the CPU progress
                if (this.cpuLog.isEmpty() || this.cpuLog.get(this.cpuLog.size() - 1).getProcessID() != this.readyQueue.get(0).getProcessID()) {
                    this.cpuLog.add(new Quantum(
                            this.readyQueue.get(0).getProcessID(),
                            this.currentTime,
                            this.currentTime + 1
                    ));
                } else {
                    this.cpuLog.get(this.cpuLog.size() - 1).setFinishTime(this.currentTime + 1);
                }
                // Incrementing the Age for all processes
                for (Process process : this.readyQueue) {
                    process.setAge(this.currentTime + 1 - process.getArrivalTime());
                }
                // Reset the age of the running process
                this.readyQueue.get(0).setAge(0);
                // Delete the process if it finished
                if (this.readyQueue.get(0).getRemainingTime() == 0) {
                    // Terminate the process
                    this.readyQueue.remove(0);
                    finished++;
                }
            }
            // Next Quantum
            this.currentTime++;
        }
    }
}