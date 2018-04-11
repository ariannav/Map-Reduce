# Author: Arianna Vacca
#Creates the Q7 visualization.

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
    #Gather city names and values from Q7 Hadoop Output
    cities = []
    values = []
    #Open given filename
    with open(filename) as f:
        for line in f:
            data = line.split('\t')

            if data[0][0] == 'F':
                #FBI Crime Data
                continue;

            city = data[0][15:-1]

            #Alternate adding newline to graph.
            if len(cities)%2 == 0:
                city = '\n' + city

            #Add values to the graph data.
            cities.append(city)
            values.append(data[1])

        #Add the given overall average. Was 0 for the airline data.
        cities.append('\nOverall')
        values.append(0)

    #Number of groups is the top ten cities.
    n_groups = len(cities)

    #Published data from FBI Crime in US Data per city.
    published_data = (1334, 966, 321, 634, 1119, 585, 1077, 707, 1214, 392, 456)

    #Initialize the graph.
    fig, ax = plt.subplots()

    #Arrange by group.
    index = np.arange(n_groups)
    bar_width = 0.45

    #State opacity of color.
    opacity = 0.6
    error_config = {'ecolor': '0.3'}

    #Set bar attributes for the q7 data bars.
    rects1 = ax.bar(index, values, bar_width,
                    alpha=opacity, color='g', error_kw=error_config,
                    label='Airline Data 1987-2008')

    #Set bar attributes for the fbi.gov data.
    rects2 = ax.bar(index + bar_width, published_data, bar_width,
                    alpha=opacity, color='y', error_kw=error_config,
                    label='FBI Crime Data (Average Per 100k Individuals; 2008)')

    # Data for comparison gathered from FBI Crime In the US Data
    # Source: https://ucr.fbi.gov/crime-in-the-u.s/

    #X-Axis and Y-axis label
    ax.set_xlabel('City Name')
    ax.set_ylabel('Rate of Crime')
    ax.set_title('Total Airport Security Incidents vs. FBI Crime Data')

    #Distance between ticks, labels of ticks, margin on either side of bars.
    ax.set_xticks(index + bar_width)
    ax.set_xticklabels(cities)
    ax.set_xmargin(0)

    #Set axis limits automatically, autoscale the x-axis scale.
    ax.set_xlim(auto = True)
    ax.autoscale_view(scaley = True)

    #Display legend, present in tight layout format, save image to q7fig.png
    ax.legend()
    fig.tight_layout()
    plt.savefig('q7fig')


if __name__ == "__main__": main()