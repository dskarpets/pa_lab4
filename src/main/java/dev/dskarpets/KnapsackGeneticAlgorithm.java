package dev.dskarpets;

import java.util.*;

public class KnapsackGeneticAlgorithm {

    static final int CAPACITY = 250;
    static final int NUM_ITEMS = 100;
    static final int POPULATION_SIZE = 100;
    static final int MAX_GENERATIONS = 1000;
    static final double MUTATION_RATE = 0.05;

    static class Item {
        int weight;
        int value;

        public Item(int weight, int value) {
            this.weight = weight;
            this.value = value;
        }
    }

    static class Individual {
        boolean[] chromosome;
        int fitness;

        public Individual(boolean[] chromosome) {
            this.chromosome = chromosome;
            calculateFitness();
        }

        public void calculateFitness() {
            int totalWeight = 0, totalValue = 0;
            for (int i = 0; i < chromosome.length; i++) {
                if (chromosome[i]) {
                    totalWeight += items[i].weight;
                    totalValue += items[i].value;
                }
            }
            fitness = (totalWeight <= CAPACITY) ? totalValue : 0;
        }
    }

    static Item[] items = new Item[NUM_ITEMS];
    static Random random = new Random();

    public static void main(String[] args) {
        generateItems();
        List<Individual> population = initializePopulation();
        Individual bestIndividual = null;

        for (int generation = 1; generation <= MAX_GENERATIONS; generation++) {
            population = evolve(population);
            Individual currentBest = Collections.max(population, Comparator.comparingInt(i -> i.fitness));
            if (bestIndividual == null || currentBest.fitness > bestIndividual.fitness) {
                bestIndividual = currentBest;
            }

            if (generation <= 20 || generation % 20 == 0) {
                System.out.printf("Iteration %d: Best fitness = %d%n", generation, bestIndividual.fitness);
            }
        }

        System.out.println("Final Best Fitness: " + bestIndividual.fitness);
    }

    static void generateItems() {
        for (int i = 0; i < NUM_ITEMS; i++) {
            int weight = random.nextInt(25) + 1;
            int value = random.nextInt(29) + 2;
            items[i] = new Item(weight, value);
        }
    }

    static List<Individual> initializePopulation() {
        List<Individual> population = new ArrayList<>();
        for (int i = 0; i < POPULATION_SIZE; i++) {
            boolean[] chromosome = new boolean[NUM_ITEMS];
            int randomIndex = random.nextInt(NUM_ITEMS);
            chromosome[randomIndex] = true; // Тільки один предмет
            population.add(new Individual(chromosome));
        }
        return population;
    }

    static List<Individual> evolve(List<Individual> population) {
        List<Individual> newPopulation = new ArrayList<>();

        while (newPopulation.size() < POPULATION_SIZE) {
            Individual parent1 = selectParent(population);
            Individual parent2 = selectParent(population);

            Individual child = crossover(parent1, parent2);
            mutate(child);
            localImprovement(child);

            newPopulation.add(child);
        }

        return newPopulation;
    }

    static Individual selectParent(List<Individual> population) {
        int tournamentSize = 5;
        List<Individual> tournament = new ArrayList<>();
        for (int i = 0; i < tournamentSize; i++) {
            tournament.add(population.get(random.nextInt(POPULATION_SIZE)));
        }
        return Collections.max(tournament, Comparator.comparingInt(i -> i.fitness));
    }

    static Individual crossover(Individual parent1, Individual parent2) {
        boolean[] childChromosome = new boolean[NUM_ITEMS];
        int splitPoint = (int) (NUM_ITEMS * 0.3); // Фіксований розподіл 30% і 70%
        for (int i = 0; i < NUM_ITEMS; i++) {
            if (i < splitPoint) {
                childChromosome[i] = parent1.chromosome[i];
            } else {
                childChromosome[i] = parent2.chromosome[i];
            }
        }
        return new Individual(childChromosome);
    }


    static void mutate(Individual individual) {
        if (random.nextDouble() < MUTATION_RATE) {
            int index1 = random.nextInt(NUM_ITEMS);
            int index2 = random.nextInt(NUM_ITEMS);
            boolean temp = individual.chromosome[index1];
            individual.chromosome[index1] = individual.chromosome[index2];
            individual.chromosome[index2] = temp;
        }
        individual.calculateFitness();
    }

    static void localImprovement(Individual individual) {
        int totalWeight = 0;
        for (int i = 0; i < NUM_ITEMS; i++) {
            if (individual.chromosome[i]) {
                totalWeight += items[i].weight;
            }
        }

        for (int i = 0; i < NUM_ITEMS; i++) {
            if (!individual.chromosome[i] && totalWeight + items[i].weight <= CAPACITY) {
                individual.chromosome[i] = true;
                totalWeight += items[i].weight;
            } else if (individual.chromosome[i]) {
                for (int j = 0; j < NUM_ITEMS; j++) {
                    if (!individual.chromosome[j] && items[j].value > items[i].value &&
                        totalWeight - items[i].weight + items[j].weight <= CAPACITY) {
                        individual.chromosome[i] = false;
                        individual.chromosome[j] = true;
                        totalWeight = totalWeight - items[i].weight + items[j].weight;
                        break;
                    }
                }
            }
        }
        individual.calculateFitness();
    }

}
