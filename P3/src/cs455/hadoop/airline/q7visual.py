# Credit: Josh Hemann

import sys
import numpy as np
import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt
from matplotlib.ticker import MaxNLocator
from collections import namedtuple

def main():
    processFile(sys.argv[1])

def processFile(filename):
    cities = []
    values = []
    with open(filename) as f:
        for line in f:
            data = line.split('\t')
            city = data[0][1:-1]
            if len(cities)%2 == 0:
                city = '\n' + city
            cities.append(city)
            values.append(data[1])
        cities.append('\nOverall')
        values.append(0)

    n_groups = len(cities)

    published_data = (1334, 966, 321, 634, 1119, 585, 1077, 707, 1214, 392, 456)

    fig, ax = plt.subplots()

    index = np.arange(n_groups)
    bar_width = 0.45

    opacity = 0.4
    error_config = {'ecolor': '0.3'}

    rects1 = ax.bar(index, values, bar_width,
                    alpha=opacity, color='g', error_kw=error_config,
                    label='Airline')

    rects2 = ax.bar(index + bar_width, published_data, bar_width,
                    alpha=opacity, color='y', error_kw=error_config,
                    label='Crime')

    ax.set_xlabel('City')
    ax.set_ylabel('Rate')
    ax.set_title('Airport security incidents with relation to city crime rates')
    ax.set_xticks(index + bar_width)
    ax.set_xticklabels(cities)
    ax.set_xmargin(0)
    ax.set_xlim(auto = True)
    ax.autoscale_view(scaley = True)
    ax.legend()
    fig.tight_layout()
    plt.savefig('myfig')


if __name__ == "__main__": main()