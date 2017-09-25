/**
 * Created by zhuhan on 16/7/8.
 */
import com.joptimizer.functions.ConvexMultivariateRealFunction;
import com.joptimizer.functions.LinearMultivariateRealFunction;
import com.joptimizer.optimizers.JOptimizer;
import com.joptimizer.optimizers.OptimizationRequest;
import org.apache.commons.math3.optim.MaxIter;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.linear.*;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import scpsolver.lpsolver.LinearProgramSolver;
import scpsolver.lpsolver.SolverFactory;
import scpsolver.problems.LPSolution;
import scpsolver.problems.LPWizard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class LP {
    public static void main(String[] args) throws java.lang.Exception{
        //parameters
        int N = 12;
        double lambda = 1.;
        int random_min = 6000;
        int random_max = 10000;

        //data prepare
        Random random = new Random();
        double[] A = new double[N];
        for(int i = 0;i < N;++i){
            A[i] = random.nextInt(random_max) % (random_max - random_min + 1) + random_min;
        }
        double[] B = new double[N*(N-1)/2];
        for(int i = 0;i < N*(N-1)/2;++i){
            B[i] = lambda;
        }
        double[] alpha = new double[N];
        double[] beta = new double[N];
        random_min = 0;
        random_max = 5000;
        for(int i = 0;i < N;++i){
            double rNum = random.nextInt(random_max) % (random_max - random_min + 1) + random_min;
            alpha[i] = (A[i] - rNum) / A[i];
            beta[i] = (A[i] + rNum) / A[i];
        }

        double[] co = new double[N + N*(N-1)/2];
        for(int i = 0;i < N;++i){
            co[i] = -A[i];
        }
        for(int i = 0;i < N*(N-1)/2;++i){
            co[i+N] = B[i];
        }

        double[][] G = new double[2*N+N*(N-1)][N+N*(N-1)/2];
        double[] h = new double[2*N+N*(N-1)];
        for(int i = 0;i < 2*N;++i){
            if(i % 2 == 0){
                G[i][i/2] = -1.;
                h[i] = -alpha[i/2];
            }
            else{
                G[i][i/2] = 1;
                h[i] = beta[i/2];
            }
        }
        int cnt = 0;
        for(int i = 0;i < N;++i){
            for(int j = i+1;j < N;++j){
                double y = A[i] > A[j] ? 1. : -1.;
                G[2*N+cnt][N+cnt] = -1.;
                G[2*N+cnt][i] = -1. * y * A[i];
                G[2*N+cnt][j] = 1. * y * A[j];
                h[2*N+cnt] = -1.;
                cnt++;
            }
        }
        for(int i = 0;i < N*(N-1)/2;++i){
            G[2*N+N*(N-1)/2+i][N+i] = -1.;
            h[2*N+N*(N-1)/2+i] = 0.;
        }

        LinearObjectiveFunction obj = new LinearObjectiveFunction(co, 0);
        Collection<LinearConstraint> cnsts = new ArrayList<LinearConstraint>();
        for(int i = 0;i < 2*N+N*(N-1);++i){
            cnsts.add(new LinearConstraint(G[i], Relationship.LEQ, h[i]));
        }

        SimplexSolver solver = new SimplexSolver();
        long startTime = System.currentTimeMillis();
        PointValuePair optSolution = solver.optimize(obj, new
                        LinearConstraintSet(cnsts),
                GoalType.MINIMIZE, new
                        NonNegativeConstraint(false),
                new MaxIter(10000));
        long endTime = System.currentTimeMillis();


        double[] solution;
        solution = optSolution.getPoint();
        System.out.println("=====");
        for(int i = 0;i < solution.length;++i){
            System.out.println(solution[i]);
        }
        System.out.println("=====");
        System.out.println(optSolution.getValue());
        System.out.println("=====");
        System.out.println("程序运行时间：" + (endTime - startTime) + "ms");    //输出程序运行时间
        System.out.println("=====");

//        LinearMultivariateRealFunction objectiveFunction = new LinearMultivariateRealFunction(co, 0);
//        ConvexMultivariateRealFunction[] inequalities = new ConvexMultivariateRealFunction[2*N+N*(N-1)];
//        for(int i = 0;i < 2*N+N*(N-1);++i){
//            inequalities[i] = new LinearMultivariateRealFunction(G[i], -h[i]);
//        }
//
//        //optimization problem
//        OptimizationRequest or = new OptimizationRequest();
//        or.setF0(objectiveFunction);
//        or.setFi(inequalities);
//        //or.setInitialPoint(new double[] {0.0, 0.0});//initial feasible point, not mandatory
//        or.setToleranceFeas(1.E-5);
//        or.setTolerance(1.E-5);
//
//        //optimization
//        JOptimizer opt = new JOptimizer();
//        opt.setOptimizationRequest(or);
//        int returnCode = opt.optimize();
//        System.out.println("return code " + returnCode);
//
//        double[] sol = opt.getOptimizationResponse().getSolution();
//        System.out.println(sol.length);
//        LPWizard lpw = new LPWizard();
//        for(int i = 0;i < N+N*(N-1)/2;++i){
//            String var = "x" + Integer.toString(i);
//            lpw.plus(var, co[i]);
//        }
//
//        for(int i = 0;i < 2*N;++i){
//            String cnst = "c" + Integer.toString(i);
//            String var = "x" + Integer.toString(i/2);
//            if(i % 2 == 0){
//                lpw.addConstraint(cnst, alpha[i/2], "<=").plus(var, 1.);
//            }
//            else{
//                lpw.addConstraint(cnst, beta[i/2], ">=").plus(var, 1.);
//            }
//        }
//
//        cnt = 0;
//        for(int i = 0;i < N;++i){
//            for(int j = i+1;j < N;++j){
//                double y = A[i] > A[j] ? 1. : -1.;
//                String cnst = "c" + Integer.toString(2*N+cnt);
//                String var1 = "x" + Integer.toString(N+cnt);
//                String var2 = "x" + Integer.toString(i);
//                String var3 = "x" + Integer.toString(j);
//                lpw.addConstraint(cnst, 1., "<=").plus(var1, 1.).plus(var2, y*A[i]).plus(var3, -y*A[j]);
//                cnt++;
//            }
//        }
//
//        cnt = 0;
//        for(int i = 0;i < N;++i){
//            for(int j = i+1;j < N;++j){
//                String cnst = "c" + Integer.toString(2*N+N*(N-1)/2+cnt);
//                String var = "x" + Integer.toString(N+cnt);
//                lpw.addConstraint(cnst, 0., "<=").plus(var, 1.);
//                cnt++;
//            }
//        }
//
//        lpw.setMinProblem(true);
//        long startTime = System.currentTimeMillis();
//        LPSolution sol = lpw.solve();
//        long endTime = System.currentTimeMillis();
//        double object = sol.getObjectiveValue();
//        System.out.println("=====");
//        System.out.println("程序运行时间：" + (endTime - startTime) + "ms");    //输出程序运行时间
//        System.out.println("=====");
//        System.out.println(object);
//        System.out.println("=====");
//        for(int i = 0;i < N+N*(N-1)/2;++i){
//            String var = "x" + Integer.toString(i);
//            System.out.println(sol.getDouble(var));
//        }
//        System.out.println("=====");
//        for(int i = 0;i < N;++i){
//            System.out.println(A[i]);
//        }
//        System.out.println("=====");
//        for(int i = 0;i < N;++i){
//            System.out.println(beta[i]*A[i]);
//        }
//        System.out.println("=====");
//        double ans = A[0] > A[1] ? 1. : -1.;
//        ans = ans * (A[0] * sol.getDouble("x0") - A[1] * sol.getDouble("x1"));
//        System.out.println(ans);
//        System.out.println("=====");
//        List<Advertisement> li = new ArrayList<Advertisement>();
//        Advertisement ad = new Advertisement();
//        ad.newScore = 1.;
//        li.add(ad);
//        List<Advertisement> li2 = new ArrayList<Advertisement>();
//        li2.add(li.get(0));
//        li2.get(0).newScore = 2.;
//        System.out.println(li.get(0).newScore);
    }
}