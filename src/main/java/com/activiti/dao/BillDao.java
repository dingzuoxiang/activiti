package com.activiti.dao;

import com.activiti.model.Bill;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 业务管理
 * @author Administrator
 *
 */
public interface BillDao {
    public List<Bill> billPage(Map<String,Object> map);

    public int billCount(Map<String,Object> map);

    public int addBill(Bill bill);

    public Bill findById(int id);

    public int updateBill(Bill bill);

    public Bill getBillByTaskId(String processInstanceId);
}
