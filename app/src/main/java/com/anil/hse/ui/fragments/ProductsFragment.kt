package com.anil.hse.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.anil.hse.R
import com.anil.hse.model.product.Product
import com.anil.hse.ui.adapter.ProductAdapter
import com.anil.hse.viewmodel.ProductsViewModel
import kotlinx.android.synthetic.main.fragment_products.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProductsFragment : Fragment() {
    private val productsViewModel: ProductsViewModel by viewModel()

    private val adapter by lazy {
        ProductAdapter(
            { onProductDetail(it) },
            { onProductAddToCart(it) })
    }
    private val categoryId by lazy {
        arguments?.let { ProductsFragmentArgs.fromBundle(it).categoryId }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_products, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        categoryId?.let { productsViewModel.setCategory(it) }
        productsViewModel.state.observe(viewLifecycleOwner, Observer {
            //  progress_bar.visibility = if (viewModel.listIsEmpty() && state == State.LOADING) View.VISIBLE else View.GONE

        })
        productsViewModel.products.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })

        productsViewModel.cart.observe(viewLifecycleOwner, Observer {
            val quantity = it.map { cartEntity -> cartEntity.quantity }.sum()
            if (quantity > 0) {
                layoutCart.visibility = View.VISIBLE
                textViewCartItems.text = quantity.toString()
            } else {
                layoutCart.visibility = View.GONE
            }
        })

        recyclerviewProducts.apply {
            adapter = this@ProductsFragment.adapter
            layoutManager = LinearLayoutManager(this@ProductsFragment.context)
        }

        layoutCart.setOnClickListener { findNavController().navigate(ProductsFragmentDirections.actionProductsFragmentToCartFragment()) }
        productsViewModel.reloadCartItems()
    }

    private fun onProductDetail(product: Product) {
        val directions =
            ProductsFragmentDirections.actionProductsFragmentToProductDetailsFragment(
                product.sku
            )
        findNavController().navigate(directions)
    }

    private fun onProductAddToCart(product: Product) {
        productsViewModel.addItemInCart(product, 1)
    }
}
