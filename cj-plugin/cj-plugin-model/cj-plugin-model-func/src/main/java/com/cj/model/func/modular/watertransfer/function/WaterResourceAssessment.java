package com.cj.model.func.modular.watertransfer.function;
import com.cj.model.func.modular.watertransfer.entity.Excel2;
import com.cj.model.func.modular.watertransfer.req.AppraiseReq;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class WaterResourceAssessment {

    public String WaterResourceAssessment(AppraiseReq req1,AppraiseReq req2) throws Exception {
       String appraise=new String();
       if (req1.getStartTime().equals(req2.getStartTime())&&req1.getEndTime().equals(req2.getEndTime()))
       {
           //比例
           double proportion1=0;
           double proportion2=0;

           //缺额
           double waterLack1=0;
           double waterLack2=0;

           List<Excel2> data1 = req1.getExcel2Data();
           List<Excel2> data2 = req2.getExcel2Data();
           proportion1=getSum(data1)[0];
           waterLack1=getSum(data1)[1];
           proportion2=getSum(data2)[0];
           waterLack2=getSum(data2)[1];
           //奇数评价为方案1，偶数为方案2
           String appraise1=new String();
           String appraise2=new String();

           String appraiseLzz1=getStance(data1,"楼庄子生活");
           String appraiseLzz2=getStance(data2,"楼庄子生活");

           String appraiseHy1=getStance(data1,"红岩生活");
           String appraiseHy2=getStance(data2,"红岩生活");

           String appraiseIndustry1=getStance(data1,"工业");
           String appraiseIndustry2=getStance(data2,"工业");
            //从供水比例比较
           if (proportion1>proportion2)
           {
               appraise1="方案1的供水比例整体结构优于方案2";
           }
           if (proportion1<proportion2)
           {
               appraise1="方案1的供水比例整体结构不如方案2";
           }
           if (proportion1==proportion2)
           {
               appraise1="两个方案总供水比例相同";
           }
            //从供水缺额比较
           if (waterLack1>waterLack2)
           {
               appraise2="方案2的供水缺额要小于方案1";
           }
           if (waterLack1<waterLack2)
           {
               appraise2="方案2的供水缺额要大于方案1";
           }
           if (waterLack1==waterLack2)
           {
               appraise2="两个方案的供水缺额相同";
           }
           Date[] tmie=getTime(data1);

           appraise="方案一："+appraise1+","+appraiseLzz1+","+appraiseHy1+","+appraiseIndustry1+";"+"方案二："+appraise2+","+appraiseLzz2+","+appraiseHy2+","+appraiseIndustry2+"。";
       }
       else
       {
           appraise="方案1与方案2配水时间设置不一致";
       }

      return appraise;
    }
    public Date[] getTime(List<Excel2> data)
    {
        List<Date> Time = new ArrayList<>();

        Time = data.stream().filter(n -> n.getStationType().equals("楼庄子生活")).map(Excel2::getTime).distinct().collect(Collectors.toList());
        Date[]time=new Date[Time.size()];
        for (int i = 0; i < Time.size(); i++)
        {
           time[i]=Time.get(i);
        }
        return time;
    }

    public String getStance(List<Excel2> data,String location)
    {
        List<Double> proportion = new ArrayList<>();
        List<Double> waterLack = new ArrayList<>();
        String x=location;
        if (location.equals("工业")){
            for (int i = 0; i < data.size(); i++)
            {
                if (data.get(i).getStationType().equals(location)&&data.get(i).getStationName().equals("八钢"))
                {
                    x=data.get(i).getStationName();
                    proportion.add(data.get(i).getProportion()) ;
                    waterLack.add(data.get(i).getWaterLack());
                }
            }
        }
        else
            {
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i).getStationType().equals(location)) {
                    x = data.get(i).getStationName();
                    proportion.add(data.get(i).getProportion());
                    waterLack.add(data.get(i).getWaterLack());
                }
            }
        }
        List<Double> num = new ArrayList<>();

        Date[]time=getTime(data);
        for (int i = 0; i < proportion.size(); i++)
        {
            if (proportion.get(i)!=1)
            {
                double n=i;
                num.add(n);
            }
        }
        // 使用 SimpleDateFormat 定义日期格式
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<String> stringList = new ArrayList<>();
        String combinedStringWithDelimiter =new String();
        String result=new String();
        if (num.size()!=0){
        for (int i = 0; i < num.size(); i++)
        {
            double n=num.get(i);
            stringList.add(dateFormat.format(time[(int)n]));
        }
        String delimiter = ", "; // 自定义分隔符，例如逗号和空格
        combinedStringWithDelimiter = stringList.stream().collect(Collectors.joining(delimiter));
        result=x+combinedStringWithDelimiter+"这些时间段并未完全满足要求";
        }
        else
        {
            result=x+"在配水时间段内都能满足要求" ;
        }
        return result;
    }
    public double[] getSum(List<Excel2> data1)
    {
        double proportion1=0;
        double waterLack1=0;
        double[]sum=new double[2];
        for (int i = 0; i < data1.size(); i++)
        {
            if (data1.get(i).getStationType().equals("楼庄子生活"))
            {
                proportion1+= data1.get(i).getProportion();
                waterLack1+= data1.get(i).getWaterLack();
            }
            if (data1.get(i).getStationType().equals("红岩生活"))
            {
                proportion1+= data1.get(i).getProportion();
                waterLack1+= data1.get(i).getWaterLack();
            }
            if (data1.get(i).getStationType().equals("工业"))
            {
                proportion1+= data1.get(i).getProportion();
                waterLack1+= data1.get(i).getWaterLack();
            }
            if (data1.get(i).getStationType().equals("总西干渠"))
            {
                proportion1+= data1.get(i).getProportion();
                waterLack1+= data1.get(i).getWaterLack();
            }
            if (data1.get(i).getStationType().equals("总东干渠"))
            {
                proportion1+= data1.get(i).getProportion();
                waterLack1+= data1.get(i).getWaterLack();
            }
         }

        sum[0]=proportion1;
        sum[1]=waterLack1;
        return sum;
    }
}
