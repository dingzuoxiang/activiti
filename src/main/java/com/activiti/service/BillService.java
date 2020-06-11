package com.activiti.service;

import com.activiti.model.Bill;

import java.util.List;
import java.util.Map;

public interface BillService {
    public List<Bill> billPage(Map<String,Object> map);

    public int billCount(Map<String,Object> map);

    public int addBill(Bill bill);
    public Bill findById(int id);

    public int updateBill(Bill bill);

    public Bill getBillByTaskId(String processInstanceId);
}
