package com.kyrie.service.impl;

import com.alibaba.excel.EasyExcel;
import com.kyrie.listener.ExcelReadListener;
import com.kyrie.pojo.ExcelExportQueryWapperDTO;
import com.kyrie.pojo.Product;
import com.kyrie.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    ExcelReadListener excelReadListener;

    /**
     * 读excel并存到数据量
     *
     * @param multipartFile
     */
    @Override
    public void readExcelAndSave(MultipartFile multipartFile) {
        try {
            //得到读工作簿对象.工作簿对象.读
            //文件，对应的实体类，监听器，读表几，默认是0，开始读
            //这个监听器里面重写了方法：1.每读一行做什么操作，2.读完了后做什么操作
            EasyExcel.read(multipartFile.getInputStream(), Product.class, excelReadListener).sheet().doRead();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 查询出符合条件的产品，封装成List集合，导出
     *
     * @param response 响应
     */
    @Override
    public void exportExcel(HttpServletResponse response, ExcelExportQueryWapperDTO dto) {

        try {
            //设置导出的文件为excel格式
//            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
//            response.setCharacterEncoding("utf-8");
//            String fileName = URLEncoder.encode("测试导出产品", "UTF-8").replaceAll("\\+","%20");
//            response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xlsx");

            //设置导出的文件为excel格式
            response.setContentType("application/vnd.ms-excle");
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode("测试导出产品", "UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + fileName + ".xlsx");

            ServletOutputStream outputStream = response.getOutputStream();
            //根据dto规则筛选查询出符合条件的数据，转成List<Product>集合
            List<Product> products = queryProduct(dto);
            //导出
            EasyExcel.write(outputStream, Product.class).sheet().doWrite(products);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 根据前端的查询条件查询出对象，封装到list中
     *
     * @param dto
     * @return
     */
    private List<Product> queryProduct(ExcelExportQueryWapperDTO dto) {
        List<Product> products = new ArrayList<>();

        for (int i = 1; i < 10; i++) {
            Product product1 = new Product("水果架", "五点" + i, "描述" + i, "B000" + i, "SKU" + i, "DE", new Date(),"忽略的字段"+i);
            products.add(product1);
        }
        return products;
    }
}
