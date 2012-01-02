package com.ayosec.procfs;

public class Proc {
  private int pid;
  private ProcStat lastProcStat;
  private CPUStat lastCPUStat;

  public Proc(int pid) {
    this.pid = pid;
    this.lastProcStat = null;
    this.lastCPUStat = null;
  }

  public int getPID() {
    return pid;
  }

  public ProcStat getStat() throws java.io.IOException, ProcStat.InvalidStatFormat {
    return new ProcStat(FileHelper.read("/proc/" + this.pid + "/stat"));
  }

  public ProcStatMemory getStatMemory() throws java.io.IOException {
    return new ProcStatMemory(FileHelper.read("/proc/" + this.pid + "/statm"));
  }

  // CPU usage
  public double getCPUUsage() {
    ProcStat procStat;
    CPUStat cpuStat;

    try {
      procStat = this.getStat();
      cpuStat = new SystemStat().getTotalCPU();
    } catch (ProcStat.InvalidStatFormat e) {
      // Ignore
      return -1;
    } catch (java.io.IOException e) {
      // Ignore
      return -2;
    }

    double cpuUsage = 0d;

    if(this.lastCPUStat != null && this.lastProcStat != null) {
      cpuUsage =
        ( 100d * (
           (procStat.getUtime() + procStat.getStime()) -
           (this.lastProcStat.getUtime() + this.lastProcStat.getStime())
        )) / (cpuStat.getTotalTime() - this.lastCPUStat.getTotalTime());
    }

    this.lastCPUStat = cpuStat;
    this.lastProcStat = procStat;

    return cpuUsage;
  }
}
