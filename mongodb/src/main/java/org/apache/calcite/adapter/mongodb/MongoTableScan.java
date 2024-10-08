/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.calcite.adapter.mongodb;

import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelOptCost;
import org.apache.calcite.plan.RelOptPlanner;
import org.apache.calcite.plan.RelOptRule;
import org.apache.calcite.plan.RelOptTable;
import org.apache.calcite.plan.RelTraitSet;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.core.TableScan;
import org.apache.calcite.rel.metadata.RelMetadataQuery;
import org.apache.calcite.rel.type.RelDataType;

import com.google.common.collect.ImmutableList;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

import static java.util.Objects.requireNonNull;

/**
 * Relational expression representing a scan of a MongoDB collection.
 *
 * <p> Additional operations might be applied,
 * using the "find" or "aggregate" methods.
 */
public class MongoTableScan extends TableScan implements MongoRel {
  final MongoTable mongoTable;
  final @Nullable RelDataType projectRowType;

  /**
   * Creates a MongoTableScan.
   *
   * @param cluster        Cluster
   * @param traitSet       Traits
   * @param table          Table
   * @param mongoTable     MongoDB table
   * @param projectRowType Fields and types to project; null to project raw row
   */
  protected MongoTableScan(RelOptCluster cluster, RelTraitSet traitSet,
      RelOptTable table, MongoTable mongoTable,
      @Nullable RelDataType projectRowType) {
    super(cluster, traitSet, ImmutableList.of(), table);
    this.mongoTable = requireNonNull(mongoTable, "mongoTable");
    this.projectRowType = projectRowType;
    checkArgument(getConvention() == MongoRel.CONVENTION);
  }

  @Override public RelNode copy(RelTraitSet traitSet, List<RelNode> inputs) {
    assert inputs.isEmpty();
    return this;
  }

  @Override public RelDataType deriveRowType() {
    return projectRowType != null ? projectRowType : super.deriveRowType();
  }

  @Override public @Nullable RelOptCost computeSelfCost(RelOptPlanner planner,
      RelMetadataQuery mq) {
    // scans with a small project list are cheaper
    final float f =
        projectRowType == null ? 1f
            : (float) projectRowType.getFieldCount() / 100f;
    final RelOptCost cost = requireNonNull(super.computeSelfCost(planner, mq));
    return cost.multiplyBy(.1 * f);
  }

  @Override public void register(RelOptPlanner planner) {
    planner.addRule(MongoToEnumerableConverterRule.INSTANCE);
    for (RelOptRule rule : MongoRules.RULES) {
      planner.addRule(rule);
    }
  }

  @Override public void implement(Implementor implementor) {
    implementor.mongoTable = mongoTable;
    implementor.table = table;
  }
}
