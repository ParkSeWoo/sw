package com.nhis.comm.model;

import com.nhis.comm.context.AppSetting;
import com.nhis.comm.context.Timestamper;
import com.nhis.comm.context.orm.DefaultRepository;
import com.nhis.comm.context.orm.SystemRepository;
import com.nhis.comm.model.master.Holiday;
import com.nhis.comm.model.member.Login;
import com.nhis.comm.model.member.Staff;
import com.nhis.comm.model.member.StaffAuthority;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import com.nhis.comm.util.DateUtils;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
  * 데이터 생성을위한 지원 구성 요소.
  * <p> 테스트 및 개발시 간이 마스터 데이터 생성을 목적으로하고 있기 때문에 실전에서의 이용은 상정하고 있지 않습니다.
  */

@Setter
public class DataFixtures {
    
    public static final String Prefix = "extension.datafixture";

    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private DefaultRepository rep;
    @Autowired
    @Qualifier(DefaultRepository.BeanNameTx)
    private PlatformTransactionManager tx;
    @Autowired
    private SystemRepository repSystem;
    @Autowired
    @Qualifier(SystemRepository.BeanNameTx)
    private PlatformTransactionManager txSystem;

    @PostConstruct
    public void initialize() {
        new TransactionTemplate(txSystem).execute((status) -> {
            initializeInTxSystem();
            return true;
        });
        new TransactionTemplate(tx).execute((status) -> {
            initializeInTx();
            return true;
        });
    }

    public void initializeInTxSystem() {
        String day = DateUtils.dayFormat(LocalDate.now());
        new AppSetting(Timestamper.KeyDay, "system", "영업일", day).save(repSystem);
    }

    public void initializeInTx() {
        String ccy = "JPY";

        // 직원: admin (pass도 같음)
        staff("admin").save(rep);

        // 자사기관
        //selfFiAcc("cashOut", ccy).save(rep);

        // 계좌 : com.nhis.api (pass도 마찬가지)
        String idSample = "com.nhis.api";
        /*acc(idSample).save(rep);*/
        login(idSample).save(rep);
        //fiAcc(idSample, "cashOut", ccy).save(rep);
    }


    public Login login(String id) {
        Login m = new Login();
        m.setId(id);
        m.setLoginId(id);
        m.setPassword(encoder.encode(id));
        return m;
    }


    // master

    /** 직원생성 */
    public Staff staff(String id) {
        Staff m = new Staff();
        m.setId(id);
        m.setName(id);
        m.setPassword(encoder.encode(id));
        return m;
    }

    /** 직원권한생성 */
    public List<StaffAuthority> staffAuth(String id, String... authority) {
        return Arrays.stream(authority).map((auth) -> new StaffAuthority(null, id, auth)).collect(Collectors.toList());
    }

    /** 공휴일생성 */
    public Holiday holiday(String dayStr) {
        Holiday m = new Holiday();
        m.setCategory(Holiday.CategoryDefault);
        m.setName("휴일");
        m.setDay(DateUtils.day(dayStr));
        return m;
    }

    // account

    /** 계좌의 빠른 생성 */
    /*public Account acc(String id) {
        Account m = new Account();
        m.setId(id);
        m.setName(id);
        m.setMail("hoge@example.com");
        m.setStatusType(AccountStatusType.Normal);
        return m;
    }*/

    /** 자사 금융 기관 계좌의 빠른 생성 */
    /*public SelfFiAccount selfFiAcc(String category, String currency) {
        SelfFiAccount m = new SelfFiAccount();
        m.setCategory(category);
        m.setCurrency(currency);
        m.setFiCode(category + "-" + currency);
        m.setFiAccountId("xxxxxx");
        return m;
    }*/

    /** 계좌에 연결된 금융 기관 계좌의 빠른 생성 */
    /*public FiAccount fiAcc(String accountId, String category, String currency) {
        FiAccount m = new FiAccount();
        m.setAccountId(accountId);
        m.setCategory(category);
        m.setCurrency(currency);
        m.setFiCode(category + "-" + currency);
        m.setFiAccountId("FI" + accountId);
        return m;
    }*/
}
