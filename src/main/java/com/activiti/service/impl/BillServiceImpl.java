package com.activiti.service.impl;

import com.activiti.dao.BillDao;
import com.activiti.model.Bill;
import com.activiti.service.BillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service("billService")
public class BillServiceImpl implements BillService {
    @Resource
    private BillDao billDao;

    @Override
    public List<Bill> billPage(Map<String, Object> map) {
        return billDao.billPage(map);
    }

    @Override
    public int billCount(Map<String, Object> map) {
        return billDao.billCount(map);
    }

    @Override
    public int addBill(Bill bill) {
        return billDao.addBill(bill);
    }

    @Override
    public Bill findById(int id) {
        return billDao.findById(id);
    }

    @Override
    public int updateBill(Bill bill) {
        return billDao.updateBill(bill);
    }

    @Override
    public Bill getBillByTaskId(String processInstanceId) {
        return billDao.getBillByTaskId(processInstanceId);
    }
}
