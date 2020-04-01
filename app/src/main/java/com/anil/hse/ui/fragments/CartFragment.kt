package com.anil.hse.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.anil.hse.R
import com.anil.hse.persistance.entitiy.CartEntity
import com.anil.hse.ui.adapter.CartAdapter
import com.anil.hse.viewmodel.CartViewModel
import kotlinx.android.synthetic.main.fragment_cart.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.math.roundToInt

class CartFragment : Fragment() {

    private val cartViewModel: CartViewModel by viewModel()
    private lateinit var navigation: NavController
    private val cartAdapter by lazy {
        CartAdapter(
            onSelected = { select -> this.onSelected(select) },
            onRemove = { remove -> onRemove(remove) },
            onAdded = { add -> onAdded(add) })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_cart, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cartViewModel.cart.observe(viewLifecycleOwner, Observer { entityList ->
            if (entityList.isNotEmpty()) {
                cartAdapter.cartItems = entityList as MutableList<CartEntity>
                cartAdapter.notifyDataSetChanged()
                val totalAmount =
                    entityList.map { it.price.toDouble() * it.quantity }.sumByDouble { it }
                        .roundToInt()
                val total = entityList.map { it.quantity }.sum()
                textViewItemCount.text = total.toString()
                textViewTotalAmount.text = getString(
                    R.string.price,
                    totalAmount.toString()
                )
            } else {
                activity?.onBackPressed()
            }
        })
        recyclerViewCartItems.apply {
            adapter = this@CartFragment.cartAdapter
            layoutManager = LinearLayoutManager(context)
        }

        buttonCheckout.setOnClickListener { cartViewModel.checkout() }
        buttonClearCart.setOnClickListener { cartViewModel.clearCart() }
        cartViewModel.loadCart()
    }

    private fun onAdded(cartEntity: CartEntity) {
        cartEntity.quantity = cartEntity.quantity + 1
        cartViewModel.updateCart(cartEntity)
    }

    private fun onRemove(cartEntity: CartEntity) {
        cartEntity.quantity = cartEntity.quantity - 1
        cartViewModel.updateCart(cartEntity)
    }

    private fun onSelected(cartEntity: CartEntity) {
        val direction =
            CartFragmentDirections.actionCartFragmentToProductDetailsFragment(cartEntity.productId)
        findNavController().navigate(direction)
    }
}
