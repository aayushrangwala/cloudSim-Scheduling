package org.cloudbus.cloudsim.examples;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.lists.VmList;
import org.cloudbus.cloudsim.*;

import java.util.Iterator;
import java.util.List;
import java.util.Collections;

public class OurBroker extends DatacenterBroker
{
    OurBroker(String name) throws Exception {
        super(name);
    }
    @Override
    protected void submitCloudlets() {
        int vmIndex = 0;
        List<org.cloudbus.cloudsim.examples.OurCloudlet> cloudletList = this.getCloudletList();
        Collections.sort(cloudletList);
        Iterator i$ = cloudletList.iterator();

        while(true) {
            Cloudlet cloudlet;
            while(i$.hasNext()) {
                cloudlet = (Cloudlet)i$.next();
                Vm vm;
                if(cloudlet.getVmId() == -1) {
                    vm = (Vm)this.getVmsCreatedList().get(vmIndex);
                } else {
                    vm = VmList.getById(this.getVmsCreatedList(), cloudlet.getVmId());
                    if(vm == null) {
                        Log.printLine(CloudSim.clock() + ": " + this.getName() + ": Postponing execution of cloudlet " + cloudlet.getCloudletId() + ": bount VM not available");
                        continue;
                    }
                }

                Log.printLine(CloudSim.clock() + ": " + this.getName() + ": Sending cloudlet " + cloudlet.getCloudletId() + " to VM #" + vm.getId());
                cloudlet.setVmId(vm.getId());
                this.sendNow(((Integer)this.getVmsToDatacentersMap().get(Integer.valueOf(vm.getId()))).intValue(), 21, cloudlet);
                ++this.cloudletsSubmitted;
                vmIndex = (vmIndex + 1) % this.getVmsCreatedList().size();
                this.getCloudletSubmittedList().add(cloudlet);
            }

            i$ = this.getCloudletSubmittedList().iterator();

            while(i$.hasNext()) {
                cloudlet = (Cloudlet)i$.next();
                this.getCloudletList().remove(cloudlet);
            }

            return;
        }
    }
}
