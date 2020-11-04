package program;

import javafx.beans.Observable;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import schedulers.*;
import schedulers.algorithms.*;

import static program.Main.*;

public class MainScreenHandeler {

    public static void handelMainScreenActions(Pane mainScreen){

        Button firstComeFirstServed = (Button) getElementById(mainScreen, "first_come_first_served");
        Button shortestJobFirst = (Button) getElementById(mainScreen, "shortest_job_first");
        Button shortestRemainingTimeFirst = (Button) getElementById(mainScreen, "shortest_remaining_time_first");
        Button explicitPriorityWithPreemption = (Button) getElementById(mainScreen, "explicit_priority_with_preemption");
        Button explicitPriorityWithoutPreemption = (Button) getElementById(mainScreen, "explicit_priority_without_preemption");
        Button roundRobin = (Button) getElementById(mainScreen, "round_robin");
        Button multiprogrammingWithUniformIoPercentage = (Button) getElementById(mainScreen, "multiprogramming_with_uniform_io_percentage");
        HBox hbox1 = (HBox) getElementById(mainScreen, "hbox1");
        TextField timeQuantum = (TextField) getElementById(hbox1,"time_quantum");
        TextField ageFactor = (TextField) getElementById(mainScreen,"age_factor") ;
        CheckBox autoTimeQuantum = (CheckBox) getElementById(mainScreen,"auto_time_quantum");
        CheckBox autoAgeFactor = (CheckBox) getElementById(mainScreen,"auto_age_factor") ;
        MenuButton IO = (MenuButton) getElementById(mainScreen,"io_percentage");

        timeQuantum.setDisable(true);
        autoTimeQuantum.setDisable(true);
        ageFactor.setDisable(true);
        autoAgeFactor.setDisable(true);
        IO.setDisable(true);

        firstComeFirstServed.setOnAction(
                event ->{
                    Scheduler firstComeFirstServedScheduler = new FirstComeFirstServedScheduler(processes);
                    Result result = new Result(firstComeFirstServedScheduler);
                    try {
                        result.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        );
        shortestJobFirst.setOnAction(
                event ->{
                    Scheduler shortestJobFirstScheduler = new ShortestJobFirstScheduler(processes);
                    Result result = new Result(shortestJobFirstScheduler);
                    try {
                        result.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        );
        shortestRemainingTimeFirst.setOnAction(
                event ->{
                    Scheduler shortestRemainingTimeFirstScheduler = new ShortestRemainingTimeFirstScheduler(processes);
                    Result result = new Result(shortestRemainingTimeFirstScheduler);
                    try {
                        result.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        );
        explicitPriorityWithPreemption.setOnAction(
                event ->{
                    setAutoOrUserEntryForPriorityScheduler(autoAgeFactor,ageFactor,true);
                }
        );
        explicitPriorityWithoutPreemption.setOnAction(
                event ->{
                    setAutoOrUserEntryForPriorityScheduler(autoAgeFactor,ageFactor,false);
                }
        );
        roundRobin.setOnAction(
                event -> {
                    if (autoTimeQuantum != null) {
                        setAutoOrUserEntryForRoundRobinScheduler(autoTimeQuantum,timeQuantum);
                    }
                }
        );
        multiprogrammingWithUniformIoPercentage.setOnAction(
                event -> {
                    IO.setDisable(false);
                }
        );

        ObservableList<MenuItem> items = IO.getItems();
        items.forEach((item) -> {
            item.setOnAction(
                    event2 -> {
                        IO.setDisable(true);
                        int IOPercent = Integer.parseInt(item.getText());
                        Scheduler multiprogrammedWithUniformIOPercentageScheduler = new MultiprogrammedWithUniformIOPercentage(processes,IOPercent);
                        Result result = new Result(multiprogrammedWithUniformIOPercentageScheduler);
                        try {
                            result.show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
            );
        }
        );


    }

    public static void setAutoOrUserEntryForRoundRobinScheduler(CheckBox auto , TextField userEnrty ){
        auto.setDisable(false);
        userEnrty.setDisable(false);
        auto.setOnAction(
                event1 -> {
                    if (auto.isSelected()) {
                        userEnrty.setDisable(true);
                        // just for now
                        Scheduler roundRobinScheduler = new RoundRobinScheduler(processes, findTimeQuantum(processes));
                        Result result = new Result(roundRobinScheduler);
                        try {
                            result.show();
                            auto.setSelected(false);
                            auto.setDisable(true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else { // no need
                        userEnrty.setDisable(false);
                    }
                }
        );

        userEnrty.setOnAction( // enter
                event2 -> {
                    auto.setDisable(true);
                    Scheduler roundRobinScheduler = new RoundRobinScheduler(processes, Double.parseDouble(userEnrty.getText()));
                    Result result = new Result(roundRobinScheduler);
                    try {
                        result.show();
                        // timeQuantum.setText("");
                        userEnrty.clear();
                        userEnrty.setDisable(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        );
    }

    public static void setAutoOrUserEntryForPriorityScheduler(CheckBox auto , TextField userEnrty , boolean flag){ // flag = true ==> with preemption , else without
        auto.setDisable(false);
        userEnrty.setDisable(false);
        auto.setOnAction(
                event1 -> {
                    if (auto.isSelected()) {
                        userEnrty.setDisable(true);
                        // just for now
                        Scheduler scheduler ;
                        if(flag)
                            scheduler = new PreemptiveExplicitPriorityScheduler(processes, findAgeFactor(processes));
                        else
                            scheduler = new NonPreemptiveExplicitPriorityScheduler(processes, findAgeFactor(processes));
                        Result result = new Result(scheduler);
                        try {
                            result.show();
                            auto.setSelected(false);
                            auto.setDisable(true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else { // no need
                        userEnrty.setDisable(false);
                    }
                }
        );

        userEnrty.setOnAction( // enter
                event2 -> {
                    auto.setDisable(true);
                    Scheduler scheduler ;
                    if(flag)
                        scheduler = new PreemptiveExplicitPriorityScheduler(processes, Long.parseLong(userEnrty.getText()));
                    else
                        scheduler = new NonPreemptiveExplicitPriorityScheduler(processes, Long.parseLong(userEnrty.getText()));
                    Result result = new Result(scheduler);
                    try {
                        result.show();
                        userEnrty.clear();
                        userEnrty.setDisable(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        );
    }

}