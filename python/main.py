from __future__ import print_function, division

import numpy as np
import matplotlib.pyplot as plt

from skimage.io import imread
from skimage import data_dir
from skimage.transform import radon, iradon
from transform import radon_mine, radon_transform

image = imread(data_dir + "/phantom.png", as_grey=True)

fig, (ax1, ax2, ax3, ax4) = plt.subplots(1, 4, figsize=(16, 4.5))

ax1.set_title("Original")
ax1.imshow(image, cmap=plt.cm.Greys_r)

theta = np.linspace(0., 180., max(image.shape), endpoint=False)
sinogram = radon(image, theta=theta, circle=True)
ax2.set_title("Radon transform\n(Sinogram)")
ax2.set_xlabel("Projection angle (deg)")
ax2.set_ylabel("Projection position (pixels)")
ax2.imshow(sinogram, cmap=plt.cm.Greys_r,
           extent=(0, 180, 0, sinogram.shape[0]), aspect='auto')

reconstruction_fbp = iradon(sinogram, theta=theta, circle=True)
ax3.set_title("Reconstruction\nFiltered back projection")
ax3.imshow(reconstruction_fbp, cmap=plt.cm.Greys_r)


'''sinogram_mine = radon_mine(image,theta)
ax4.set_title("My sinogram")
ax4.imshow(sinogram_mine, cmap=plt.cm.Greys_r,
           extent=(0, 180, 0, sinogram_mine.shape[0]), aspect='auto')
'''

radon_transform(image, 2, 15, 45)


fig.tight_layout()
plt.show()