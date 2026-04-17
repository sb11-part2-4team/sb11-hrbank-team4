package com.sb11.hr_bank.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling // 스프링 스케쥴러를 활성화 -> @Scheduled를 감지하여 fixedRateString 옵션 값마다(실행 주기) 실행
public class SchedulerOn {

}
