package xyz.chendai.tools.platonrecover.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.chendai.tools.platonrecover.client.RecoverPlatOnClient;
import xyz.chendai.tools.platonrecover.config.RecoverProperties;
import xyz.chendai.tools.platonrecover.data.RecoverRepository;
import xyz.chendai.tools.platonrecover.data.WarnAddressRepository;
import xyz.chendai.tools.platonrecover.entity.Recover;
import xyz.chendai.tools.platonrecover.entity.WarnAddress;
import xyz.chendai.tools.platonrecover.service.RecoverService;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RecoverServiceImpl implements RecoverService {

    @Autowired
    private RecoverRepository recoverRepository;
    @Autowired
    private WarnAddressRepository warnAddressRepository;
    @Autowired
    private RecoverPlatOnClient recoverPlatOnClient;
    @Autowired
    private RecoverProperties recoverProperties;

    @Override
    public void initCheck() {
        log.info("账户检测开始");
        List<Recover> recoverList = recoverRepository.findAllByBalanceGreaterThanAndTypeEqualsAndStatusEquals(BigInteger.ZERO, Recover.Type.NORMAL, Recover.Status.PEDDING);
        List<WarnAddress> insertList = recoverList
                .parallelStream()
                .map(recover -> {
                    try {
                        BigInteger chainBalance = recoverPlatOnClient.platonGetBalance(recover.getAddress());
                        return WarnAddress.builder().address(recover.getAddress()).balance(chainBalance).build();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .filter(warnAddress -> warnAddress.getBalance().compareTo(BigInteger.ZERO) > 0)
                .collect(Collectors.toList());
        if (insertList.size() > 0) {
            warnAddressRepository.saveAll(insertList);
            log.info("账户检测异常！有些账户存在余额，请在数据库中warn_address表查看明细");
        } else {
            log.info("账户检测正常！所有账户余额都为0");
        }
    }

    @Override
    public void recover() throws Exception {
        log.info("余额恢复开始");
        while (hasRecoverRecord()){
            recoverRecord();
        }
        log.info("余额恢复结束");


    }

    private boolean hasRecoverRecord() {
        return  true;
    }

    private void recoverRecord() {
        List<Recover> recoverList = recoverRepository.findAllByBalanceGreaterThanAndTypeEqualsAndStatusEquals(BigInteger.ZERO, Recover.Type.NORMAL, Recover.Status.PEDDING);

        recoverList.parallelStream()
                .forEach(recover -> {


                });
    }

    @Override
    public void check() throws Exception {


    }
}
