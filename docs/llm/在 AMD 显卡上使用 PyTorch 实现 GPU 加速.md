# Torch 环境

- [Torch 环境](#torch-环境)
  - [在 AMD 显卡上使用 PyTorch 实现 GPU 加速](#在-amd-显卡上使用-pytorch-实现-gpu-加速)
    - [使用 ROCm 支持的 PyTorch 版本](#使用-rocm-支持的-pytorch-版本)
  - [使用 CPU 作为备选方案](#使用-cpu-作为备选方案)

## 在 AMD 显卡上使用 PyTorch 实现 GPU 加速

要在 AMD 显卡上使用 PyTorch（`torch`），需通过以下方案实现 GPU 加速：

### 使用 ROCm 支持的 PyTorch 版本

1. 安装 AMD ROCm 驱动（需 Linux 系统，如 Ubuntu 24.04）：
```bash
wget https://repo.radeon.com/amdgpu-install/6.4.3/ubuntu/noble/amdgpu-install_6.4.60403-1_all.deb
sudo apt install ./amdgpu-install_6.4.60403-1_all.deb
sudo apt update
sudo apt install python3-setuptools python3-wheel
sudo usermod -a -G render,video $LOGNAME # Add the current user to the render and video groups
sudo apt install rocm
```

2. 安装 AMD GPU 驱动
```bash
wget https://repo.radeon.com/amdgpu-install/6.4.3/ubuntu/noble/amdgpu-install_6.4.60403-1_all.deb
sudo apt install ./amdgpu-install_6.4.60403-1_all.deb
sudo apt update
sudo apt install "linux-headers-$(uname -r)" "linux-modules-extra-$(uname -r)"
sudo apt install amdgpu-dkms
```

3. 安装支持 ROCm 的 PyTorch：
```bash
pip install torch torchvision torchaudio --index-url https://download.pytorch.org/whl/rocm6.4
```

1. 验证是否启用 GPU：
```python
import torch
print(torch.cuda.is_available())  # 应返回 True（ROCm 模拟 CUDA 接口）
```

## 使用 CPU 作为备选方案

若无法启用 GPU，可直接使用 CPU 运行 PyTorch：

```python
import torch
device = "cpu"
tensor = torch.randn(3, 3).to(device)
```
