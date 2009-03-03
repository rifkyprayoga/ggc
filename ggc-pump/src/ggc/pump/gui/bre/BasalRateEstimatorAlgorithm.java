package ggc.pump.gui.bre;

import ggc.pump.data.bre.BREData;
import ggc.pump.data.bre.BREDataCollection;
import ggc.pump.data.bre.BasalEstimationData;
import ggc.pump.data.graph.bre.BREGraphsAbstract;

import java.util.ArrayList;
import java.util.Hashtable;

public class BasalRateEstimatorAlgorithm 
{
    //GraphViewBasalRateEstimator m_gv;
    Hashtable<String, BREGraphsAbstract> m_graphs;
    BREDataCollection data_collection;
    
    
    public BasalRateEstimatorAlgorithm(Hashtable<String, BREGraphsAbstract> gvs)
    {
        this.m_graphs = gvs;
    }

    
    public void setData(BREDataCollection data_coll)
    {
        data_collection = data_coll;
        // do algorithm data
        //m_gv.setData(data_coll);
        m_graphs.get("" + BasalRateEstimator.GRAPH_OLD_RATE).setData(data_coll);
        
        m_graphs.get("" + BasalRateEstimator.GRAPH_RATIO).setData(data_coll);
        m_graphs.get("" + BasalRateEstimator.GRAPH_BASALS).setData(data_coll);
        
        processNewBasals();
        
        m_graphs.get("" + BasalRateEstimator.GRAPH_NEW_RATE).setData(data_coll);
        
    }
    

    double lifetime_insulin = 4; // 4 hours for fastacting insulin
    int INTERVAL = 15;
    
    
    private void processNewBasals()
    {
        ArrayList<BREData> data = this.data_collection.getDataByType(BREData.BRE_DATA_BASAL_NEW);
        
        ArrayList<BasalEstimationData> beds = new ArrayList<BasalEstimationData>(); 
        
        int hour = 0;
        int minute = 0;
        int index = 0;
        
        // create intervals
        while (hour!=24)
        {
            BasalEstimationData bed = new BasalEstimationData();
            bed.time = hour * 100 + minute;
            
            minute += INTERVAL;
            
            if (minute >= 60)
            {
                minute -= 60;
                hour++;
            }
            
            if (!data.get(index).areWeInTimeRange(bed.time))
            {
                index++;
            }
            
            if (!data.get(index).areWeInTimeRange(bed.time))
            {
                System.out.println("Collection error !!!!");
            }
            else
            {
                bed.basal_value = ((double)data.get(index).value) * (INTERVAL/60.0d);
            }
            
            beds.add(bed);
        }
        
        
        
        
        
        double calc_const = Math.pow(Math.E, (-1.0d) * 1/lifetime_insulin * (INTERVAL/60.0d));
        
        
        System.out.println("Beds: " + beds.size());
        
        double prev = 0.0d;
        double val;
        
        for(int i=0; i<beds.size(); i++)
        {
            if (prev==0.0d)
            {
                beds.get(i).insulin_value = beds.get(i).basal_value;
                prev = beds.get(i).basal_value;
            }
            else
            {
                val = beds.get(i).basal_value;
                val += prev;
                
                beds.get(i).insulin_value = val * calc_const;
                prev = beds.get(i).insulin_value;
            }
        }
        

        
        for(int i=0; i<beds.size(); i++)
        {
            System.out.println(beds.get(i).toString());
        }
        
        this.data_collection.setBasalEstimationData(beds);
        // calculation is done each 15 minutes, which means that hour basal
        
            //java.lang.Math m = new Math();
        
        
        //    double u = x * m.pow(m.E, 2 * 0.25);
        
    }
    
    
    
    
    
}

