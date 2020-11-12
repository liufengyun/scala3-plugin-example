package scala.instrumentation

import dotty.tools.dotc._

import plugins._

import core._
import Contexts._
import Symbols._
import Flags._
import SymDenotations._

import Decorators._
import ast.Trees._
import ast.tpd
import StdNames.nme
import Names._
import Constants.Constant

import scala.language.implicitConversions


class InstrumentStart(setting: Setting) extends PluginPhase {
  import tpd._

  val phaseName = "instrumentStart"

  private var enterSym: Symbol = _
  private var exitSym: Symbol = _
  private var truncateSym: Symbol = _

  override val runsAfter = Set("pickler")
  override val runsBefore = Set("instrumentFinish")

  override def prepareForUnit(tree: Tree)(using Context): Context =
    val runtime = requiredModule(setting.runtimeObject)
    enterSym = runtime.requiredMethod("enter")
    exitSym = runtime.requiredMethod("exit")
    truncateSym = runtime.requiredMethod("truncate")
    ctx

  private def createEnterTree(methId: Int, owner: Symbol)(using Context): ValDef = {
    val enterTree = ref(enterSym).appliedTo(Literal(Constant(methId)))
    val valDefSym = newSymbol(owner, "frameId".toTermName, Synthetic, enterTree.tpe)
    ValDef(valDefSym, enterTree)
  }

  override def transformDefDef(tree: DefDef)(using Context): Tree =
    if !setting.instrumentable(tree) then return tree

    val sym = tree.symbol
    val methId = setting.add(tree)
    val enterTree = createEnterTree(methId, sym)
    val exitTree = ref(exitSym).ensureApplied
    val truncateTree = ref(truncateSym).appliedTo(ref(enterTree.symbol))

    object MapReturn extends TreeMap {
      override def transform(tree: Tree)(using Context): Tree = tree match {
        case tree: Return =>
          // label block
          if tree.from.symbol.is(Label) then return tree

          if tree.expr.tpe =:= defn.UnitType then
            cpy.Block(tree)(tree.expr :: Nil, Return(exitTree, EmptyTree))
          else
            val valDefSym = newSymbol(sym, "res".toTermName, Synthetic, tree.expr.tpe)
            val valDef = ValDef(valDefSym, tree.expr)
            cpy.Block(tree)(valDef :: exitTree :: Nil, Return(ref(valDefSym), EmptyTree))

        case tree: Try =>
          val cases1 = tree.cases.map { caseDef =>
            cpy.CaseDef(caseDef)(caseDef.pat, caseDef.guard, Block(truncateTree :: Nil, caseDef.body))
          }
          cpy.Try(tree)(tree.expr, cases1, tree.finalizer)

        case ddef: DefDef => ddef
        case tdef: TypeDef => tdef
        case tree => super.transform(tree)
      }
    }

    def rhs1 = MapReturn.transform(tree.rhs) match
      case rhs @ Block(stats, Literal(Constant(()))) =>
        cpy.Block(rhs)(enterTree :: stats , exitTree)

      case rhs @ Block(stats, expr) =>
        if expr.tpe =:= defn.UnitType then
          cpy.Block(rhs)((enterTree :: stats) :+ expr , exitTree)
        else
          val valDefSym = newSymbol(sym, "res".toTermName, Synthetic, expr.tpe)
          val valDef = ValDef(valDefSym, expr)
          cpy.Block(rhs)((enterTree :: stats) :+ valDef :+ exitTree, ref(valDefSym))

      case rhs =>
        if rhs.tpe =:= defn.UnitType then
          cpy.Block(rhs)(enterTree :: rhs :: Nil, exitTree)
        else
          val valDefSym = newSymbol(sym, "res".toTermName, Synthetic, rhs.tpe)
          val valDef = ValDef(valDefSym, rhs)
          cpy.Block(rhs)(enterTree :: valDef :: exitTree :: Nil, ref(valDefSym))

    cpy.DefDef(tree)(rhs = rhs1)

  end transformDefDef
}

class InstrumentFinish(setting: Setting) extends PluginPhase { thisPhase =>
  import tpd._

  override def phaseName: String = "instrumentFinish"

  override val runsAfter = Set("instrumentStart")
  override val runsBefore = Set("erasure")

  private var initSym: Symbol = _
  private var finishSym: Symbol = _
  private var dumped: Boolean = false

  override def prepareForUnit(tree: Tree)(using Context): Context =
    if !dumped then
      dumped = true
      setting.writeMethods()

    val runtime = requiredModule(setting.runtimeObject)
    initSym = runtime.requiredMethod("init")
    finishSym = runtime.requiredMethod("finish")
    ctx

  override def transformDefDef(tree: DefDef)(using Context): Tree =
    if ctx.platform.isMainMethod(tree.symbol) then
      val size = setting.nextId()
      val initTree = ref(initSym).appliedTo(Literal(Constant(size)))
      val finishTree = ref(finishSym).appliedTo(Literal(Constant(setting.runtimeOutputFile)))
      val rhs1 = Block(initTree :: tree.rhs :: Nil, finishTree)
      cpy.DefDef(tree)(rhs = rhs1)
    else tree
}
